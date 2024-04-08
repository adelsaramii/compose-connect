package navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import di.provideViewModel
import presentation.SharedAppDataImpl
import presentation.common.themes.ChirrioAppTheme
import presentation.conversation.ConversationViewModel
import presentation.conversation.components.ChirrioScaffold
import presentation.conversation.components.ConversationContent
import presentation.conversation.components.EmptyStartScreen
import presentation.drawer.DrawerViewModel
import presentation.login_screen.components.LoginScreen
import presentation.login_screen.components.SignupScreen
import presentation.profile.ProfileViewModel
import presentation.profile.components.ProfileScreen

const val NAVIGATION_TIMEOUT = 700

@Composable
fun SharedNavigatedApp() {
    ChirrioAppTheme {
/*        val navigator = rememberSharedNavigator()
        CompositionLocalProvider(LocalNavigator provides navigator) {
            AnimatedContent(
                targetState = LocalNavigator.current?.screens,
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                transitionSpec = {
                    fadeIn(tween(NAVIGATION_TIMEOUT)) togetherWith fadeOut(
                        tween(
                            NAVIGATION_TIMEOUT
                        )
                    )
                }
            ) { currentScreen ->
                when (currentScreen) {
                    is Screens.Login -> {
                        ChirrioScaffold(false) {
                            LoginScreen()
                        }
                    }

                    is Screens.Signup -> {
                        ChirrioScaffold(false) {
                            SignupScreen()
                        }
                    }

                    is Screens.Chat -> {
                        ChirrioScaffold {
                            val shared: SharedAppDataImpl = provideViewModel()
                            val drawer: DrawerViewModel = provideViewModel()
                            drawer.setChatId(currentScreen.id)
                            val chatViewModel = remember {
                                ConversationViewModel(
                                    shared,
                                    currentScreen.id
                                )
                            }
                            ConversationContent(chatViewModel)
                        }
                    }

                    is Screens.Profile -> {
                        ChirrioScaffold {
                            val drawer: DrawerViewModel = provideViewModel()
                            drawer.setUserId(currentScreen.id)
                            val shared: SharedAppDataImpl = provideViewModel()
                            val viewModel =
                                remember { ProfileViewModel(shared, currentScreen.id) }
                            ProfileScreen(viewModel)
                        }
                    }

                    is Screens.Main -> {
                        ChirrioScaffold {
                            EmptyStartScreen()
                        }
                    }

                    null -> Unit
                }
            }
        }*/
        CallDialog(onAnswer = {

        }, onDecline = {

        })
    }
}

@Composable
fun CallDialog(
    onAnswer: () -> Unit,
    onDecline: () -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Color.Gray)) {
        Column(
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Incoming Call")
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Adel is calling...")
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        onDecline()
                    }
                ) {
                    Text(text = "Decline")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        onAnswer()
                    }
                ) {
                    Text(text = "Answer")
                }
            }
        }
    }
}