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
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
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
fun TrailerPrecheck(driver: String?, navController: NavController){
    var meter by remember { mutableStateOf(false) }
    var checklist by remember { mutableStateOf<List<InspectionItem>?>(trailerinspectionChecklist) }
    var updatedChecklist by remember { mutableStateOf(checklist) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Example functions to handle actions
    val showMeter: (Boolean) -> Unit = { isMeterShown ->
        meter=isMeterShown
    }
    var loading by remember { mutableStateOf(false) }
    val onSubmit: (List<InspectionItem>) -> Unit = { updatedItems ->
        // Handle updated items, e.g., save to database or perform further actions
        updatedChecklist = updatedItems
        loading = true
       scope.launch {
           LoginHandler().sendTrailerdata(driver!!,updatedItems){res, failed ->

               if(failed){
                   loading = false
                   scope.launch {
                       snackbarHostState.showSnackbar("Failed to Send Data")

                   }


               }
               else {
                   loading = true
                   scope.launch {
                       snackbarHostState.showSnackbar("Data send Successfully")
                       navController.popBackStack()
                   }
               }
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
            .background(gradientBrush)
    ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .wrapContentWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,

                        ) {
                        Text(
                            text = "Trailer Pre Check",
                            fontSize = 24.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TrailerInspectionChecklistScreen(checklist!!, showMeter, onSubmit)

                    }
                }


            }
        }
    }
    SnackbarHost(hostState = snackbarHostState)
}
@Composable
fun TrailerInspectionChecklistScreen(
    checklist: List<InspectionItem>,
    showmeter:(ismeter:Boolean)->Unit,
    onSubmit:(updated:List<InspectionItem>)->Unit

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
                        updatedChecklist=checklist.map { if(it==item) it.copy(status = newStatus) else it }

                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                onSubmit(updatedChecklist!!)


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

