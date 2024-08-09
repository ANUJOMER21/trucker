import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import org.lighthousegames.logging.logging
import viewmodel.LoginHandler

@Composable
fun LoginScreen(driver: String, navController: NavHostController) {
    val log = logging("KMLogging Tag")
    log.d { "Driver ID: $driver" }

    if (driver != "0") {
        LaunchedEffect(driver) {
            onLoginSuccess(driver, navController)
        }
    } else {
        LoginScreenContent(navController)
    }
}

@Composable
private fun LoginScreenContent(navController: NavHostController) {
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
                        coroutineScope.launch {
                            LoginHandler().Login(email, password) { result, failed ->
                                if (failed) {
                                    coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Failed to Login")
                                    }
                                } else {
                                    result?.driver?.id?.let {
                                        onLoginSuccess(it, navController)
                                    }
                                }
                            }
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Please enter valid email and password")
                        }
                    }
                },
                grey = grey
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(5.dp)
        ) {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    isLoginEnabled: Boolean,
    onLoginClick: () -> Unit,
    grey: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Welcome",
            color = Color(3, 17, 79, 255),
            style = MaterialTheme.typography.h4,
            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextFieldBackground(grey) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Mobile Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "Phone icon"
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    backgroundColor = grey,
                    cursorColor = Color.Black
                )
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextFieldBackground(grey) {
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = onPasswordVisibilityChange
                    ) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Outlined.Lock else Icons.Filled.Lock,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    backgroundColor = grey,
                    cursorColor = Color.Black
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0, 18, 68, 255))
        ) {
            Text(text = "Login", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun OutlinedTextFieldBackground(
    color: Color,
    content: @Composable () -> Unit
) {
    Box {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 8.dp)
                .background(
                    color,
                    shape = RoundedCornerShape(10.dp)
                )
        )
        content()
    }
}




private fun onLoginSuccess(driverId: String, navController: NavHostController) {
    // Navigate to the DMV screen and remove the login screen from the backstack
    navController.navigate("${Screens.bmv}?driver=$driverId") {
        popUpTo("${Screens.Login}?driver=$driverId") {
            inclusive = true
        }
    }
}


