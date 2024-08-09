import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import viewmodel.DATAXX
import viewmodel.LoginHandler

@Composable
fun History(driver: String) {
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            Color(252, 163, 17, 255),
            Color(252, 163, 17, 255),
        )
    )

    Scaffold(
        modifier = Modifier.background(gradientBrush),
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientBrush)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                HistoryPage(driver)
            }
        }
    )
}

@Composable
fun HistoryPage(driver: String) {
    var data by remember { mutableStateOf<List<DATAXX>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    // Fetch data when the component is first composed
    LaunchedEffect(driver) {
        scope.launch {
            LoginHandler().gethistory(driver) { res, _ ->
                if (res?.status == "success") {
                    data = res.history.DATA
                }
                isLoading = false
            }
        }
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        elevation = 12.dp,
        backgroundColor = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            // Heading
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column {
                    Text(
                        text = "History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF333333),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    )

                    if (isLoading) {
                        Text(
                            text = "Loading...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    } else if (data.isNotEmpty()) {
                        HistoryColumn(data)
                    } else {
                        Text(
                            text = "No History Found",
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            color = Color(0xFF333333),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryColumn(data: List<DATAXX>) {
    LazyColumn(modifier = Modifier.padding(8.dp)) {
        items(data) { item ->
            InspectionCard(data = item)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun InspectionCard(data: DATAXX) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        border = BorderStroke(1.dp, Color.Black),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Date in center
            Text(
                text = data.Date,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Divider
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            // Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "Trailer Pre Check:")
                    Text(text = "Truck Pre Check:")
                    Text(text = "End Trip:")
                }
                Column {
                    Text(text = data.Data.inspection_items)
                    Text(text = data.Data.truck_precheck)
                    Text(text = data.Data.precheck_data)
                }
            }
        }
    }
}
