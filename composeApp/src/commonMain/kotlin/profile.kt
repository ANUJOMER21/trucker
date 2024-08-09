import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.launch
import viewmodel.LoginHandler
import viewmodel.profilemodel

@Composable
fun Profile(driverId: String) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var profileModel by remember { mutableStateOf<profilemodel?>(null) }

    LaunchedEffect(driverId) {
        LoginHandler().getprofile(driverId) { res, failed ->
            if (failed) {
                scope.launch {
                    snackbarHostState.showSnackbar("Failed to Send Data")
                }
            } else {
                profileModel = res
            }
        }
    }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(252, 163, 17, 255),
            Color(252, 163, 17, 255),
        )
    )

    Scaffold(
        modifier = Modifier.background(gradientBrush),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (profileModel != null) {
                    UserDetailScreen(profileModel!!)
                }
            }
        }
    )
}

@Composable
fun UserDetailScreen(user: profilemodel) {
    Card(
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.Black),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = 12.dp,
        backgroundColor = Color.White
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Heading
            Text(
                text = "User Profile",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            )
            val scrollState = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .verticalScroll(scrollState) // Make the column scrollable
            ) {
                // User Fields
                UserField(label = "ID", value = user.data.ID)
                UserField(label = "Name", value = user.data.Name)
                UserField(label = "Mobile", value = user.data.Mobile)
                UserField(label = "Email", value = user.data.Email)
                UserField(label = "Date of Birth", value = user.data.Dob)
                UserField(label = "Emergency Contact", value = user.data.EmergencyContact)
                UserField(label = "License Number", value = user.data.LicenseNumber)
            }
        }
    }
}

@Composable
fun UserField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Display the label at the top
        Text(
            text = label,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = Color(0xFF666666),
            modifier = Modifier.padding(bottom = 4.dp) // Add padding between label and TextField
        )

        // Outlined TextField that is not editable
        OutlinedTextField(
            value = value,
            onValueChange = {}, // No-op since the field is not editable
            enabled = false, // Makes the TextField non-editable
            readOnly = true, // Another way to make it non-editable
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontSize = 16.sp, color = Color(0xFF333333)),
            singleLine = true, // Optional: To ensure the TextField is a single line
            colors = TextFieldDefaults.outlinedTextFieldColors(

                backgroundColor = Color.Transparent // Transparent background
            )
        )
    }
}

