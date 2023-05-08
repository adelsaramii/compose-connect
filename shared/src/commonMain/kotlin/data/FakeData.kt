package data

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.json.Json
import resourceBindings.drawable_ali
import resourceBindings.drawable_someone_else
import resourceBindings.drawable_sticker
import themes.ThemeMode
import transport.getLocalHost
import transport.inputMessages
import transport.outputMessages
import kotlin.coroutines.EmptyCoroutineContext

val initialMessages = listOf(
    Message(
        "me",
        "Check it out!",
        "8:07 PM"
    ),
    Message(
        "me",
        "Thank you!",
        "8:06 PM",
        drawable_sticker
    ),
    Message(
        "Taylor Brooks",
        "You can use all the same stuff",
        "8:05 PM"
    ),
    Message(
        "Taylor Brooks",
        "@aliconors Take a look at the `Flow.collectAsState()` APIs",
        "8:05 PM"
    ),
    Message(
        "John Glenn",
        "Compose newbie as well, have you looked at the JetNews sample? Most blog posts end up " +
                "out of date pretty fast but this sample is always up to date and deals with async " +
                "data loading (it's faked but the same idea applies) \uD83D\uDC49" +
                "https://github.com/android/compose-samples/tree/master/JetNews",
        "8:04 PM"
    ),
    Message(
        "me",
        "Compose newbie: I’ve scourged the internet for tutorials about async data loading " +
                "but haven’t found any good ones. What’s the recommended way to load async " +
                "data and emit composable widgets?",
        "8:03 PM"
    )
)

val exampleUiState = ConversationUiState(
    initialMessages = initialMessages,
    channelName = "#composers",
    channelMembers = 42
)

/**
 * Example colleague profile
 */
val colleagueProfile = ProfileScreenState(
    userId = "12345",
    photo = drawable_someone_else,
    name = "Taylor Brooks",
    status = "Away",
    displayName = "taylor",
    position = "Senior Android Dev at Openlane",
    twitter = "twitter.com/taylorbrookscodes",
    timeZone = "12:25 AM local time (Eastern Daylight Time)",
    commonChannels = "2"
)

/**
 * Example "me" profile.
 */
val meProfile = ProfileScreenState(
    userId = "me",
    photo = drawable_ali,
    name = "Ali Conors",
    status = "Online",
    displayName = "aliconors",
    position = "Senior Android Dev at Yearin\nGoogle Developer Expert",
    twitter = "twitter.com/aliconors",
    timeZone = "In your timezone",
    commonChannels = null
)

@Immutable
data class ProfileScreenState(
    val userId: String,
    val photo: String?,
    val name: String,
    val status: String,
    val displayName: String,
    val position: String,
    val twitter: String = "",
    val timeZone: String?, // Null if me
    val commonChannels: String?, // Null if me
) {
    fun isMe() = userId == meProfile.userId
}

class ConversationUiState(
    val channelName: String,
    val channelMembers: Int,
    initialMessages: List<Message>,
) {
    private val _messages: MutableList<Message> =
        mutableStateListOf(*initialMessages.toTypedArray())
    val messages: List<Message> = _messages

    fun addMessage(msg: Message) {
        _messages.add(0, msg) // Add to the beginning of the list
    }
}

@Stable
class AdditionalUiState {
    private val scope = CoroutineScope(EmptyCoroutineContext)
    private val client = HttpClient {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    private var session: DefaultClientWebSocketSession? = null
    init {
        scope.launch {
            withContext(Dispatchers.Default) {
                client.webSocket(method = HttpMethod.Get, host = getLocalHost(), port = 8082) {
                    session = this
                    while (true) {
                        this.ensureActive()
                    }
                }
            }
        }
    }
    private val _conversationUiState: MutableStateFlow<ConversationUiState> = MutableStateFlow(exampleUiState)
    val conversationUiState: StateFlow<ConversationUiState> = _conversationUiState
    private val _themeMode: MutableStateFlow<ThemeMode> = MutableStateFlow(ThemeMode.LIGHT)
    val themeMode: StateFlow<ThemeMode> = _themeMode
    private val _drawerShouldBeOpened: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val drawerShouldBeOpened: StateFlow<Boolean> = _drawerShouldBeOpened
    fun openDrawer() {
        _drawerShouldBeOpened.value = true
    }

    fun resetOpenDrawerAction() {
        _drawerShouldBeOpened.value = false
    }

    fun switchTheme(theme: ThemeMode) {
        _themeMode.value = theme
    }
    fun sendMessage(message: Message) {
        _conversationUiState.value.addMessage(message)
        scope.launch {
            println("Session is: $session")
            session?.sendSerialized(message)
        }
    }
    fun disconnect() {
        scope.launch {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Disconnecting"))
            client.close()
        }
    }
}

fun getWebsocket(scope: CoroutineScope) {
    scope.launch {
        withContext(Dispatchers.Default) {
            val client = HttpClient {
                install(WebSockets) {
                    contentConverter = KotlinxWebsocketSerializationConverter(Json)
                }
            }
            client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
                val messageOutputRoutine = launch { outputMessages() }
                val userInputRoutine = launch { inputMessages() }

                userInputRoutine.join() // Wait for completion; either "exit" or error
                messageOutputRoutine.cancelAndJoin()
            }
            client.close()
            println("Connection closed. Goodbye!")
        }
    }
}
