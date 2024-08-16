import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String = "Success",
    message: String
) {
    if (showDialog) {
        Surface(

            shape = RoundedCornerShape(10.dp), // Corner radius of 10dp
            elevation = 8.dp // Elevation for the dialog
        ) {
            AlertDialog(
                onDismissRequest = { }, // Prevent dismissing the dialog by tapping outside
                confirmButton = {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("OK", color = Color.White)
                    }
                },
                title = {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp
                    )
                },
                text = {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = message,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                },
                shape = RoundedCornerShape(10.dp), // Corner radius of 10dp for the AlertDialog itself
                backgroundColor =   Color(252, 163, 17, 255)
            )
        }
    }
}
