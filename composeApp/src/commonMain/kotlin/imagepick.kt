import androidx.compose.material.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import io.ktor.websocket.Frame
import org.lighthousegames.logging.logging
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun ByteArray.toBase64(): String =
    Base64.encode(this,0,this.size)



@Composable
fun imagepick(){
val scope = rememberCoroutineScope()

val singleImagePicker = rememberImagePickerLauncher(
    selectionMode = SelectionMode.Single,
    scope = scope,
    onResult = { byteArrays ->
        byteArrays.firstOrNull()?.let {
            val imageBitmap = it.toImageBitmap()
             val base64=  it.toBase64()
            val log = logging("submit Tag")
            log.d { base64 }

            // Process the selected images' ByteArrays.


        }
    }
)

Button(
onClick = {
    singleImagePicker.launch()
}
) {
    Frame.Text("Pick Single Image")
}
}