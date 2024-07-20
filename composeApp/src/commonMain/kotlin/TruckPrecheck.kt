import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import org.lighthousegames.logging.logging
import kotlinx.coroutines.launch
import viewmodel.LoginHandler

@Composable
fun TruckPrecheck(driver: String?, navController: NavController) {
    var checklist by remember { mutableStateOf<List<InspectionItem>?>(trucprecheckItems) }
    var meter by remember { mutableStateOf(false) }
    var updatedChecklist by remember { mutableStateOf(checklist) }

    // Example functions to handle actions
    val showMeter: (Boolean) -> Unit = { isMeterShown ->
        meter = isMeterShown
    }

    val onSubmit: (List<InspectionItem>) -> Unit = { updatedItems ->
        // Handle updated items, e.g., save to database or perform further actions
        updatedChecklist = updatedItems
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
            .background(gradientBrush)
            .padding(16.dp),
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
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text(
                        text = "Truck Pre Check",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    InspectionChecklistScreen(checklist ?: emptyList(), showMeter, onSubmit)
                }
            } else {
                ShowMeter(driver!!, updatedChecklist, navController)
            }
        }
    }
}

@Composable
fun InspectionChecklistScreen(
    checklist: List<InspectionItem>,
    showMeter: (Boolean) -> Unit,
    onSubmit: (updated: List<InspectionItem>) -> Unit
) {
    var updatedChecklist by remember { mutableStateOf<List<InspectionItem>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(checklist) { item ->
                InspectionItemRow(
                    item = item,
                    onStatusChange = { newStatus ->
                        updatedChecklist = checklist.map { if (it == item) it.copy(status = newStatus) else it }
                        onSubmit(checklist.map { if (it == item) it.copy(status = newStatus) else it })
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                showMeter(true)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255))
        ) {
            Text("Submit", color = Color.White)
        }
    }
}

@Composable
fun ShowMeter(driver: String, updatedChecklist: List<InspectionItem>?, navController: NavController) {
    var image by remember { mutableStateOf<ByteArray?>(null) }
    var meterReading by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

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
                .padding(8.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            meter2 {
                image = it
            }
            Spacer(modifier = Modifier.height(8.dp))
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
                    .padding(10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Black,
                    backgroundColor = Color.LightGray.copy(alpha = 0.1f),
                    cursorColor = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    loading = true
                    scope.launch {
                        if(meterReading.isNullOrEmpty()){
                            scope.launch {
                                loading=false
                                snackbarHostState.showSnackbar("please enter meter reading")
                            }
                        }
                        else if(image!!.isEmpty()){
                            scope.launch {
                                loading=false
                                snackbarHostState.showSnackbar("Please pick meter image")
                            }
                        }
                        else {
                            LoginHandler().sendTruckData(
                                driverId = driver,
                                InspectionList = updatedChecklist!!,
                                image = image!!,
                                MeterReading = meterReading
                            ) { result, failed ->
                                loading = false
                                if (failed) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to Send Data")
                                    }
                                } else {
                                    loading = true
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Data sent Successfully")
                                        navController.popBackStack()
                                    }
                                }
                            }
                        }
                        val log = logging("submit Tag")
                        log.d { "meter: $meterReading" }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255))
            ) {
                Text("Submit Form", color = Color.White)
            }
        }
    }
    SnackbarHost(hostState = snackbarHostState)
}

@Composable
fun InspectionItemRow(
    item: InspectionItem,
    onStatusChange: (InspectionStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedStatus by remember { mutableStateOf(item.status) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = item.name,
            modifier = Modifier.padding(bottom = 8.dp),
          /*  maxLines = 2,
            overflow = TextOverflow.Ellipsis,*/
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Box(
            modifier = Modifier.wrapContentSize(Alignment.TopStart)
        ) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Blue),
                border = BorderStroke(1.dp, Color.Blue)
            ) {
                Text(text = selectedStatus.name)
                Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f) // Ensure the dropdown is on top of other content
            ) {
                InspectionStatus.values().forEach { status ->
                    DropdownMenuItem(
                        onClick = {
                            selectedStatus = status
                            onStatusChange(status)
                            expanded = false
                        }
                    ) {
                        Text(text = status.name)
                    }
                }
            }
        }
    }
}


