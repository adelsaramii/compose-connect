package data

import androidx.compose.runtime.Stable
import data.repositories.RoomRepository
import data.repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import themes.ThemeMode
import transport.WsHandler
import util.uuid
import viewmodel.ViewModelPlatformImpl

@Stable
class MainViewModel : ViewModelPlatformImpl() {
    private val websocketHandler = WsHandler()

    private val _plusRoomDialogOpen = MutableStateFlow(false)
    val plusRoomDialogOpen = _plusRoomDialogOpen.asStateFlow()

    fun openRoomDialog() {
        _plusRoomDialogOpen.value = true
    }

    fun closeRoomDialog() {
        _plusRoomDialogOpen.value = false
    }

    private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
    val user = _user.asStateFlow()

    private val _loginScreenMode = MutableStateFlow(LoginScreenState.LOGIN)
    val loginScreenMode = _loginScreenMode.asStateFlow()

    fun setLoginMode(mode: LoginScreenState) {
        _loginScreenMode.value = mode
    }

    private val _errorMessage = MutableStateFlow<Resource.Error<*>?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _screenState: MutableStateFlow<AppScreenState> = MutableStateFlow(AppScreenState.CHAT)
    val screenState: StateFlow<AppScreenState> = _screenState

    private val _chats: MutableStateFlow<Map<String, ConversationUiState>> = MutableStateFlow(emptyMap())
    val chats = _chats.asStateFlow()

    private val _conversationUiState: MutableStateFlow<ConversationUiState> = MutableStateFlow(ConversationUiState.Empty)
    val conversationUiState: StateFlow<ConversationUiState> = _conversationUiState

    private val _selectedUserProfile: MutableStateFlow<ProfileScreenState?> = MutableStateFlow(null)
    val selectedUserProfile: StateFlow<ProfileScreenState?> = _selectedUserProfile

    private val _themeMode: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.DARK)
    val themeMode: StateFlow<ThemeMode> = _themeMode

    private val _drawerShouldBeOpened: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val drawerShouldBeOpened: StateFlow<Boolean> = _drawerShouldBeOpened

    fun setCurrentConversation(title: String) {
        _screenState.value = AppScreenState.CHAT
        _conversationUiState.value = chats.value.getValue(title)
        vmScope.launch(Dispatchers.Default) {
            websocketHandler.connectRoom("chat/composers/") { message ->
                _conversationUiState.value.addMessage(message)
            }
        }
    }

    fun setCurrentAccount(userId: String) {
        _screenState.value = AppScreenState.ACCOUNT
        _selectedUserProfile.value = exampleAccountsState.getValue(userId)
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    fun switchTheme(theme: ThemeMode) {
        _themeMode.value = theme
    }

    fun sendMessage(message: Message) {
        vmScope.launch {
            websocketHandler.sendMessage(message)
        }
    }

    fun loginUser(email: String, password: String) {
        vmScope.launch {
            when (val result = UserRepository.login(email, password)) {
                is Resource.Data -> {
                    _user.value = result.payload
                    when (val rooms = RoomRepository.getRoomsByUser(result.payload)) {
                        is Resource.Data -> _chats.value = rooms.payload
                        is Resource.Error -> {
                            println(rooms.message)
                            println(rooms.status)
                        }
                    }
                }

                is Resource.Error -> {
                    _user.value = null
                    _errorMessage.value = result
                }
            }
        }
    }

    fun logoutUser() {
        vmScope.launch {
            user.value?.let { user ->
                _user.value = null
                UserRepository.logout(user)
            }
        }
    }

    fun signupUser(email: String, firstName: String, lastName: String, password: String) {
        vmScope.launch {
            val result = UserRepository.signupUser(
                SignupRequest(email, firstName, lastName, password)
            )
            when (result) {
                is Resource.Data -> _user.value = result.payload
                is Resource.Error -> _errorMessage.value = result
            }
        }
    }

    fun createRoom(roomName: String) {
        user.value?.let { actingUser ->
            vmScope.launch {
                when (val room = RoomRepository.createRoom(ChatRoomCreationDto(uuid(), roomName, listOf(actingUser.email)), actingUser)) {
                    is Resource.Data<ChatRoomCreationDto> -> {
                        val new = mapOf(room.payload.id to room.payload.toConvState())
                        _chats.update { it + new }
                    }

                    is Resource.Error<ChatRoomCreationDto> -> Unit
                }
                closeRoomDialog()
            }
        }
    }
}

@Serializable
data class LoginForm(val email: String, val password: String)

enum class AppScreenState {
    CHAT,
    ACCOUNT
}

enum class LoginScreenState {
    LOGIN,
    REGISTER
}