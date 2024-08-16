import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import viewmodel.LoginHandler

@Composable
fun TrailerPrecheck(driver: String?, navController: NavController) {
    var checklist by remember { mutableStateOf<List<InspectionItem>?>(trailerinspectionChecklist) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val onSubmit: (List<InspectionItem>,String) -> Unit = { updatedItems,Remark ->
        loading = true
        scope.launch {
            LoginHandler().sendTrailerdata(driver!!,updatedItems,Remark) { res, failed ->

                if (failed) {
                    scope.launch {
                        loading = false
                        snackbarHostState.showSnackbar("Failed to Send Data")
                    }
                } else {
                    showDialog = true
                }
            }
        }
    }

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
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            if (showDialog) {
                SuccessDialog(
                    showDialog = true,
                    title = "Success",
                    message = "Trailer pre check data sent successfully.",
                    onDismiss = { navController.popBackStack() }
                )
            }

            Card(
                modifier = Modifier.wrapContentWidth(),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Trailer Pre Check",
                            fontSize = 24.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TrailerInspectionChecklistScreen(checklist!!, onSubmit)
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
    }
}

@Composable
fun TrailerInspectionChecklistScreen(
    checklist: List<InspectionItem>,
    onSubmit: (updated: List<InspectionItem>,Remark:String) -> Unit
) {
    var updatedChecklist by remember { mutableStateOf(checklist) }
    var Remark by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(updatedChecklist) { item ->
                InspectionItemRow(
                    item = item,
                    onStatusChange = { newStatus ->
                        updatedChecklist = updatedChecklist.map {
                            if (it == item) it.copy(status = newStatus) else it
                        }
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = Remark,
            onValueChange = { Remark = it },
            label = { Text("Enter Remarks") },
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
        Button(
            onClick = { onSubmit(updatedChecklist,Remark) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color(3, 17, 79, 255))
        ) {
            Text("Submit", color = Color.White)
        }
    }
}

