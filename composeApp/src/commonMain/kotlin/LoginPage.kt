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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Brush
import androidx.navigation.NavOptions
import com.example.cmppreference.LocalPreference
import com.russhwolf.settings.Settings
import viewmodel.LoginHandler

@Composable
fun LoginScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    /*val preference = LocalPreference.current
    preference.put("login", "result")*/


    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isLoginEnabled by remember { derivedStateOf { email.isNotEmpty() && password.isNotEmpty() } }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val grey = Color(245, 245, 245)

    LaunchedEffect(email, password) {
        if (email.length > 10) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Email should not exceed 10 characters")
            }
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(3, 17, 79, 255),
            Color(2, 69, 140, 255)
        )
    )

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
            // DeliveryImage at the top
            DeliveryImage()
            Spacer(modifier = Modifier.height(50.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Welcome",
                    color = Color.White,
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Normal
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextFieldBackground(grey) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            if (it.length <= 10 && it.all { char -> char.isDigit() }) {
                                email = it
                            }
                        },
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
                                imageVector = Icons.Default.Phone, // Choose an appropriate icon
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

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextFieldBackground(grey) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isLoginEnabled) {
                            coroutineScope.launch {
                                LoginHandler().Login(email,password){result,failed->
                                    if(failed){
                                        coroutineScope.launch {
                                            snackbarHostState.showSnackbar("Failed to Login")

                                        }


                                    }
                                    else{
                                        coroutineScope.launch {
                                            navController.navigateToPrecheck(driverId = result!!.driver.id)


                                        /*   // snackbarHostState.showSnackbar(result)
                                            navController.navigate(Screens.Precheck+"?driver=${result!!.driver.id}")
                                            navController.rem*/
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
                //    enabled = isLoginEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(4, 117, 255, 255))
                ) {
                    Text(text = "Login", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        SnackbarHost(hostState = snackbarHostState)
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
