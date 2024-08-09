import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import viewmodel.LoginHandler

@Composable

fun mainpage(driverid_: String,newDriver:(driverid:String)->Unit) {

    var driverId by remember { mutableStateOf<String?>(driverid_) }
    val log = logging("olddriverId Tag")
    log.d{
        driverId
    }
    MaterialTheme {
        if (driverId.equals("0")) {
            LoginScreenContent { newDriverId ->
                driverId = newDriverId
                val log = logging("newDriverId Tag")
                log.d { driverId }
                newDriver(driverId!!)
            }
        } else {
            BottomNavigationBar(driverId!!) {
                driverId = "0"
                newDriver(driverId!!)
            }
        }
    }
}


@Composable
private fun LoginScreenContent(onLoginSuccess: (driverId: String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoginEnabled by remember { derivedStateOf { email.isNotEmpty() && password.isNotEmpty() } }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(252, 163, 17, 255),
            Color(252, 163, 17, 255),
        )
    )
    val grey = Color(245, 245, 245)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxHeight()
        ) {
            DeliveryImage()
            Spacer(modifier = Modifier.height(30.dp))

            LoginForm(
                email = email,
                onEmailChange = { newEmail ->
                    if (newEmail.length <= 10 && newEmail.all { it.isDigit() }) {
                        email = newEmail
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Number should not exceed 10 characters")
                        }
                    }
                },
                password = password,
                onPasswordChange = { password = it },
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                isLoginEnabled = isLoginEnabled,
                onLoginClick = {
                    if (isLoginEnabled) {
                        handleLogin(email, password, onLoginSuccess, snackbarHostState, coroutineScope)
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter a valid email and password")
                        }
                    }
                },
                grey = grey
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

private fun handleLogin(
    email: String,
    password: String,
    onLoginSuccess: (String) -> Unit,
    snackbarHostState: SnackbarHostState,
    coroutineScope: CoroutineScope
) {
    coroutineScope.launch {
        LoginHandler().Login(email, password) { result, failed ->
            if (failed) {
                coroutineScope.launch {
                snackbarHostState.showSnackbar("Failed to Login")}
            } else {
                result?.driver?.id?.let {
                    onLoginSuccess(it)
                }
            }
        }
    }
}
