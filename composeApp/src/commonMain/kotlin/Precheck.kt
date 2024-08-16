import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import trucker.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.logging
import trucker.composeapp.generated.resources.trailer
import trucker.composeapp.generated.resources.truck
import trucker.composeapp.generated.resources.truck2
import viewmodel.LoginHandler

@Composable
fun NavPrecheck(driverId: String) {
    val log = logging("NavPrecheck Tag")
    log.d { driverId }
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.Precheck
    ) {

        composable(route=Screens.TruckPrecheck){
            TruckPrecheck(driverId,navController)
        }
        composable(route=Screens.Precheck){
            Precheck(driverId, navController)
        }
        composable(route=Screens.TrailerPrecheck){
            TrailerPrecheck(driverId, navController)
        }
        composable(route=Screens.meter){
            meter(driverId, navController)
        }

    }
}

@Composable
fun Precheck(driverId: String, navController: NavController) {
    val log = logging("Precheck Tag")
    log.d { driverId }

    var checkTruckStatus by remember { mutableStateOf(false) }
    var checkTrailerStatus by remember { mutableStateOf(false) }
    var showNextBtn by remember { mutableStateOf(false) }
    var showNextBtn2 by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
     var resetbtn by remember { mutableStateOf(false) }

    LaunchedEffect(driverId) {
        LoginHandler().getPrecheckStatus(driverId) { res, failed ->
            log.d{
                "${res.toString()}"
            }
            if (failed || res == null) {
                checkTruckStatus = true
                checkTrailerStatus = true
                showNextBtn = false
                showNextBtn2 = false
                loading = true
                resetbtn=false
            } else {
                checkTruckStatus = res.truck
                checkTrailerStatus = res.inspection
                showNextBtn =
                    (    checkTruckStatus || checkTrailerStatus) && res.endtrip

                loading = false
                resetbtn =checkTruckStatus || checkTrailerStatus
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
            Text(
                text = "Pre Check",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (loading) {
                CircularProgressIndicator()
            } else {
                if (checkTruckStatus && checkTrailerStatus) {
                    ImageContent("Complete Your Trip", Res.drawable.truck2)
                }
                else {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        PreCheckItem(
                            imageRes = Res.drawable.truck2,
                            name = "Pre Check Truck",
                            isChecked = checkTruckStatus
                        ) {
                            if (!checkTruckStatus) {
                                navController.navigate(Screens.TruckPrecheck)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PreCheckItem(
                            imageRes = Res.drawable.trailer,
                            name = "Pre Check Trailer",
                            isChecked = checkTrailerStatus
                        ) {
                            if (!checkTrailerStatus) {
                                navController.navigate(Screens.TrailerPrecheck)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            if(resetbtn) {
                Button(
                    onClick = {
                        scope.launch {
                            LoginHandler().ResetData(driverId){
                                res,failed->
                                if(failed)
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Failed to reset the data")
                                    }
                                else{
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Data is Reset")
                                        LoginHandler().getPrecheckStatus(driverId) { res, failed ->
                                            if (failed || res == null) {
                                                checkTruckStatus = true
                                                checkTrailerStatus = true
                                                showNextBtn = false
                                                loading = true
                                                resetbtn=false
                                            } else {
                                                checkTruckStatus = res.truck
                                                checkTrailerStatus = res.inspection
                                                showNextBtn =/* res.endtrip && checkTruckStatus && checkTrailerStatus*/
                                                    (checkTruckStatus || checkTrailerStatus )&& !res.endtrip
                                                loading = false
                                                resetbtn =checkTruckStatus || checkTrailerStatus
                                            }
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
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
                ) {
                    Text("Reset Data", color = Color.White)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            if (showNextBtn) {
                Button(
                    onClick = {
                        navController.navigate(Screens.meter)


                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF031151))
                ) {
                    Text("Go to End the Trip", color = Color.White)
                }
            }


            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

@Composable
fun ImageContent(text: String, imageRes: DrawableResource) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .padding(2.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun PreCheckItem(
    isChecked: Boolean,
    imageRes: DrawableResource,
    name: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF031151),
                            Color(0xFF031151)
                        )
                    )
                )
                .padding(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Image(
                painter = painterResource(imageRes),
                contentDescription = name,
                modifier = Modifier
                    .size(120.dp)
                    .padding(2.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}


