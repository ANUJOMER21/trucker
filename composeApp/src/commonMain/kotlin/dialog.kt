import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun SuccessDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    title: String = "Success",
    message: String
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { }, // Prevent dismissing the dialog by tapping outside
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("OK")
                }
            },
            title = {
                Text(text = title)
            },
            text = {
                Text(text = message)
            }
        )
    }
}
