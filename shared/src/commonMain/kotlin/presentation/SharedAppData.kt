package presentation

import domain.model.AppScreenState
import domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import presentation.common.themes.ThemeMode

interface SharedAppData {

    val user: StateFlow<User>

    fun setUser(user: User)

    val errorMessage: StateFlow<String?>

    fun setErrorMessage(message: String?)

    val screenState: StateFlow<AppScreenState>

    fun setScreenState(screen: AppScreenState)

    val theme: StateFlow<ThemeMode>

    fun switchTheme(theme: ThemeMode)
}

class SharedAppDataImpl : SharedAppData {
    private val _user: MutableStateFlow<User> = MutableStateFlow(User.Empty)
    override val user = _user.asStateFlow()
    override fun setUser(user: User) {
        _user.value = user
    }

    private val _errorMessage = MutableStateFlow<String?>(null)
    override val errorMessage = _errorMessage.asStateFlow()
    override fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }

    private val _screenState = MutableStateFlow(AppScreenState.CHAT)

    override val screenState = _screenState.asStateFlow()

    override fun setScreenState(screen: AppScreenState) {
        _screenState.value = screen
    }

    private val _themeMode: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.DARK)
    override val theme: StateFlow<ThemeMode> = _themeMode.asStateFlow()

    override fun switchTheme(theme: ThemeMode) {
        _themeMode.value = theme
    }
}