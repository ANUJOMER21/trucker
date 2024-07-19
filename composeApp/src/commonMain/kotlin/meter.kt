import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

import org.lighthousegames.logging.logging
import viewmodel.LoginHandler

@Composable
fun meter(driver: String?, navController: NavController) {
    var loading by remember { mutableStateOf(false) }
    val scope= rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(3, 17, 79, 255),
            Color(2, 69, 140, 255)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
    ) {


        fun NavHostController.navigateToLogin() {
            val options = NavOptions.Builder()
                .setPopUpTo(Screens.Login, inclusive = true) // Remove Login screen from back stack
                .build()

            this.navigate("${Screens.Login}", options)
        }

meter_caller(loading,snackbarHostState) {
    loading = true
    scope.launch {
      if(it.meter.isNullOrEmpty()){
          scope.launch {
              loading=false
              snackbarHostState.showSnackbar("Please enter meter reading")
          }
      }
        else if(it.image.isEmpty()){
          scope.launch {
              loading=false
              snackbarHostState.showSnackbar("Please pick meter image")
          }
      }
        else {
          LoginHandler().sendendtrip(
              driver!!,
              it.meter,
              it.image
          ) { res, failed ->
              loading = false
              if (failed) {
                  scope.launch {
                      snackbarHostState.showSnackbar("Failed to Send Data")
                  }
              } else {
                  loading = true
                  scope.launch {
                      snackbarHostState.showSnackbar("Data sent Successfully")
                      val options = NavOptions.Builder()
                          .setPopUpTo("${Screens.Precheck}?driver=$driver", inclusive = true) // Clear the entire back stack
                          .build()



                      navController.navigate(Screens.Login,options)


                  }
              }
          }
      }

}



}
    }
    SnackbarHost(hostState = snackbarHostState)
}

    @Serializable
    data class imagedata(val meter:String,val image:ByteArray)
@Composable
fun meter_caller(loading:Boolean,snackbarHostState: SnackbarHostState,meterread:(reading:imagedata)->Unit){

    var meterReading by remember { mutableStateOf("") }
    var image by remember { mutableStateOf<ByteArray?>(null) }
    val scope= rememberCoroutineScope()
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.wrapContentWidth(),
            elevation = 1.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Please enter meter reading",
                    fontSize = 24.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                if (loading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                else {
                    meter2() {
                        image = it


                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = meterReading,
                        onValueChange = { meterReading = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        label = { Text("Enter Meter Reading") },
                        singleLine = true,
                        //   visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

                        modifier = Modifier.fillMaxWidth()
                            .padding(10.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Black,
                            backgroundColor = Color.LightGray.copy(alpha = 0.1f),
                            cursorColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val log = logging("KMLogging Tag")

                            if(meterReading.isNullOrEmpty()){
                                scope.launch {  snackbarHostState.showSnackbar("Please enter meter reading") }

                            }
                            else{
                                log.e { "${meterReading}" }
                            meterread(imagedata(meterReading, image!!))}
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),

                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = Color(
                                4,
                                117,
                                255,
                                255
                            )
                        )

                    ) {
                        Text("Submit", color = Color.White)

                    }
                }
            }
        }
    }
}



@Composable
fun meter2(imagebase:(image:ByteArray)->Unit) {

    var selectedImage by remember { mutableStateOf<ImageBitmap?>(null) }

    val scope = rememberCoroutineScope()

    val singleImagePicker = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let {
                val imageBitmap = it.toImageBitmap()
                selectedImage=imageBitmap
                val base64=  it.toBase64()
                imagebase(it)
                val log = logging("submit Tag")
                log.d { base64 }

                // Process the selected images' ByteArrays.


            }
        }
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                selectedImage?.let {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                } ?: run {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        Text("No image selected!", color = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        singleImagePicker.launch()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(4, 117, 255, 255))
                ) {
                    Text("Choose Image", color = Color.White)
                }



            }
        }
    }
}


// Function to convert ImageBitmap to Base64 string
