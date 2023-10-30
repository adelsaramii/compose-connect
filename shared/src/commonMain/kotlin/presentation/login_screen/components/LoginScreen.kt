package presentation.login_screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chirrio.filepicker.PhotoPicker
import di.provideViewModel
import navigation.LocalNavigator
import navigation.Screens
import navigation.navigateTo
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.SharedAppData
import presentation.common.resourceBindings.drawable_user_icon
import presentation.login_screen.LoginViewModel

@Composable
fun ShowOrHideSnackbar(viewModel: SharedAppData, scaffoldState: ScaffoldState) {
    val error by viewModel.errorMessage.collectAsState()
    LaunchedEffect(error) {
        error?.let {
            scaffoldState.snackbarHostState.showSnackbar(
                message = it.message,
                actionLabel = null,
                duration = SnackbarDuration.Short
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SignupScreen(
    viewModel: LoginViewModel = provideViewModel(),
) {
    val scrollState = rememberScrollState()
    var email by rememberSaveable { mutableStateOf("") }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    val navHost = LocalNavigator.current
    val signupButtonEnabled = email.isNotBlank() &&
            firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword.isNotBlank() &&
            password == confirmPassword
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthSpacer()
        LoginHeaderText("Register")
        var showPicker by rememberSaveable { mutableStateOf(false) }
        with(LocalDensity.current) {
            val borderColor = MaterialTheme.colorScheme.primary
            val pathMeter = 5.dp.toPx()
            Box {
                Column(
                    modifier = Modifier
                        .size(150.dp)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        .padding(4.dp)
                        .drawWithCache {
                            onDrawBehind {
                                drawCircle(
                                    color = borderColor, style = Stroke(
                                        1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(
                                            floatArrayOf(pathMeter, pathMeter)
                                        )
                                    )
                                )
                            }
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(drawable_user_icon),
                        contentDescription = "Add user photo",
                        modifier = Modifier.size(100.dp).clip(CircleShape),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                }
                Column(
                    modifier = Modifier
                        .shadow(24.dp, CircleShape)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                        .align(Alignment.BottomEnd)
                        .border(1.dp, color = MaterialTheme.colorScheme.primary, CircleShape)
                        .pointerHoverIcon(PointerIcon.Hand)
                        .clickable {
                            showPicker = true
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit image",
                        modifier = Modifier.size(36.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    PhotoPicker(show = showPicker, multiplePhotos = false) {
                        println(it)
                        showPicker = false
                    }
                }
            }
        }
        LoginTextField(
            label = "Email:",
            value = email,
            onValueChange = { email = it },
        )
        LoginTextField(
            label = "First name:",
            value = firstName,
            onValueChange = { firstName = it }
        )
        LoginTextField(
            label = "Last name:",
            value = lastName,
            onValueChange = { lastName = it }
        )
        LoginTextField(
            label = "Password:",
            value = password,
            isPassword = true,
            onValueChange = { password = it }
        )
        LoginTextField(
            label = "Confirm password:",
            value = confirmPassword,
            isPassword = true,
            onValueChange = { confirmPassword = it }
        )
        AuthButton(
            enabled = signupButtonEnabled,
            text = "Sign up"
        ) {
            viewModel.signupUser(email, firstName, lastName, password)
        }
        Row(Modifier.padding(top = 32.dp)) {
            SecondaryLoginText("Already have an account?", Modifier.padding(end = 20.dp))
            ClickableSecondaryLoginText("Log in") {
                navHost?.navigateTo(Screens.Login())
            }
        }
        AuthSpacer()
    }
}

@Composable
fun AuthSpacer() = Spacer(Modifier.height(42.dp))

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = provideViewModel(),
) {
    val scrollState = rememberScrollState()
    var email by rememberSaveable { mutableStateOf("glebgytnik@gmail.com") }
    var password by rememberSaveable { mutableStateOf("LiuRuis5968!") }
    val loginButtonEnabled = email.isNotBlank() && password.isNotBlank()
    val navHost = LocalNavigator.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthSpacer()
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(1f))
            LoginHeaderText("Please log in", Modifier.weight(6f))
            Spacer(Modifier.weight(1f))
        }
        LoginTextField(
            label = "Email:",
            value = email,
            onValueChange = { email = it },
        )
        LoginTextField(
            label = "Password:",
            value = password,
            isPassword = true,
        ) { password = it }
        AuthButton(
            enabled = loginButtonEnabled,
            text = "Log in"
        ) {
            viewModel.loginUser(email, password, navHost)
        }
        Row(Modifier.padding(top = 32.dp)) {
            SecondaryLoginText("Don't have an account?", Modifier.padding(end = 20.dp))
            ClickableSecondaryLoginText("Register") {
                navHost?.navigateTo(Screens.Signup())
            }
        }
        AuthSpacer()
    }
}

@Composable
fun LoginTextField(
    label: String,
    value: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit,
) = Column {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    SecondaryLoginText(
        text = label,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp)
    )
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            if (isPassword) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = "Show password",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .pointerHoverIcon(PointerIcon.Hand)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                }
            } else Unit
            innerTextField()
        },
        modifier = Modifier
            .padding(bottom = 24.dp)
            .size(300.dp, 32.dp)
            .background(MaterialTheme.colorScheme.tertiary, CircleShape)
            .padding(8.dp),
        textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurfaceVariant),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
    )
}

@Composable
fun ClickableSecondaryLoginText(
    text: String,
    modifier: Modifier = Modifier,
    onCLick: () -> Unit,
) = SecondaryLoginText(
    text = text,
    modifier = modifier
        .clickable {
            onCLick()
        }
        .pointerHoverIcon(PointerIcon.Hand)
)


@Composable
fun SecondaryLoginText(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = modifier
    )
}

@Composable
fun AuthButton(
    enabled: Boolean,
    text: String,
    modifier: Modifier = Modifier,
    action: () -> Unit,
) {
    Button(
        enabled = enabled,
        modifier = modifier.pointerHoverIcon(PointerIcon.Hand),
        onClick = action,
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text,
            color = if (enabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LoginHeaderText(
    text: String,
    modifier: Modifier = Modifier,
) = Text(
    text = text,
    fontSize = 56.sp,
    color = MaterialTheme.colorScheme.primary,
    textAlign = TextAlign.Center,
    modifier = modifier.padding(bottom = 32.dp)
)