import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import viewmodel.LoginHandler

@Composable
fun TruckPrecheck(driver: String?, navController: NavController) {
    var checklist by remember { mutableStateOf(trucprecheckItems) }
    var meter by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(252, 163, 17, 255),
            Color(252, 163, 17, 255),
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            if (!meter) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Truck Pre Check",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.weight(1f) // This will make the LazyColumn take only available space
                    ) {
                        InspectionChecklistScreen(
                            checklist = checklist,
                            onStatusChange = { updatedItems ->
                                checklist = updatedItems

                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {

                                meter = true


                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255))
                    ) {
                        Text("Submit", color = Color.White)
                    }
                }
            } else {
               ShowMeter (driver!!, checklist, navController)
            }
        }
    }

    SnackbarHost(hostState = snackbarHostState)
}

@Composable
fun InspectionChecklistScreen(
    checklist: List<InspectionItem>,
    onStatusChange: (updated: List<InspectionItem>) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(checklist) { item ->
            InspectionItemRow(
                item = item,
                onStatusChange = { newStatus ->
                    val updatedChecklist = checklist.map {
                        if (it == item) it.copy(status = newStatus) else it
                    }
                    onStatusChange(updatedChecklist)
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InspectionItemRow(
    item: InspectionItem,
    onStatusChange: (InspectionStatus) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp)
    ) {
        Text(
            text = item.name,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            InspectionStatus.values().forEach { status ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onStatusChange(status) }
                        .padding(horizontal = 8.dp)
                ) {
                    Checkbox(
                        checked = item.status == status,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                onStatusChange(status)
                            }
                        }
                    )
                    Text(text = status.name, modifier = Modifier.padding(start = 4.dp))
                }
            }
        }
    }
}



@Composable
fun ShowMeter(driver: String, updatedChecklist: List<InspectionItem>?, navController: NavController) {
    var image by remember { mutableStateOf<ByteArray?>(null) }
    var meterReading by remember { mutableStateOf("") }
    var Remark by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showdialog by remember{ mutableStateOf(false) }
    if (loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            if(showdialog){
            SuccessDialog(showDialog = true,title="Success", message = "Truck pre check Data sent successfully.", onDismiss = {
                navController.popBackStack()
            })
            }
            meter2 { image = it }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = meterReading,
                onValueChange = { meterReading = it },
                label = { Text("Enter Meter Reading") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    backgroundColor = Color.LightGray.copy(alpha = 0.1f),
                    cursorColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = Remark,
                onValueChange = { Remark = it },
                label = { Text("Enter Remarks") },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
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

                    loading = true
                    scope.launch {
                        when {
                            meterReading.isEmpty() -> {
                                loading = false
                                snackbarHostState.showSnackbar("Please enter meter reading")
                            }
                            image == null -> {
                                loading = false
                                snackbarHostState.showSnackbar("Please pick meter image")
                            }
                            Remark.isEmpty()->{
                                loading = false
                                snackbarHostState.showSnackbar("Please Enter Remark.")

                            }
                            else -> {
                                LoginHandler().sendTruckData(
                                    driverId = driver,
                                    InspectionList = updatedChecklist!!,
                                    image = image!!,
                                    MeterReading = meterReading,
                                   remark= Remark
                                ) { result, failed ->
                                    if (failed) {
                                        loading = false
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Failed to Send Data")
                                        }
                                    } else {
                                        loading = false
                                        showdialog=true
                                    }
                                }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255))
            ) {
                Text("Submit Form", color = Color.White)
            }
        }
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



