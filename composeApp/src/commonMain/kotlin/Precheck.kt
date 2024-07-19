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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import trucker.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.logging
import trucker.composeapp.generated.resources.trailer
import trucker.composeapp.generated.resources.truck2
import viewmodel.LoginHandler

@Composable
fun Precheck(driverId: String?, navController: NavController) {
    var checktruckstatus by remember { mutableStateOf(false) }
    var checktrailerstatus by remember { mutableStateOf(false) }
    var shownextbtn by remember { mutableStateOf(checktrailerstatus && checktrailerstatus) }
    var loading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope= rememberCoroutineScope(

    )
    scope.launch {
        LoginHandler().getPrecheckStatus(driverId!!) { res, failed ->

            if (failed || res ==null) {
                checktrailerstatus=true
                checktruckstatus=true
                shownextbtn=false
                loading=true
            }
            else{
                //todo remove!
                checktrailerstatus=res.inspection
                checktruckstatus=res.truck
                shownextbtn=res.endtrip && checktrailerstatus &&checktruckstatus
                loading=false
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
                modifier = Modifier.wrapContentWidth(),
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp)
            ) {

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Pre Check",
                        fontSize = 24.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    if (loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PreCheckItem(
                                imageRes = Res.drawable.truck2,
                                name = "Pre Check Truck",
                                ichecked = checktruckstatus
                            ) {
                                val log = logging("sendTrailerData1")
                                log.d { "trailer:$checktrailerstatus,truck=$checktruckstatus" }
                                if (!checktruckstatus) {
                                    navController.navigate("${Screens.TruckPrecheck}?driver=$driverId")
                                }

                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            PreCheckItem(
                                imageRes = Res.drawable.trailer,
                                name = "Pre Check Trailer",
                                ichecked = checktrailerstatus
                            ) {
                                if (!checktrailerstatus) {
                                    navController.navigate("${Screens.TrailerPrecheck}?driver=$driverId")
                                }
                            }
                        }
                    }

                }
            }

            Spacer(modifier = Modifier.height(24.dp))


                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(4, 117, 255, 255)),
                    onClick = {
                        val log = logging("submit Tag")
                        log.d { "$shownextbtn" }
                        if(shownextbtn){
                        navController.navigate("${Screens.meter}?driver=$driverId")}
                        else{
                         scope.launch {
                             snackbarHostState.showSnackbar("Button is not activate.")
                         }
                        }
                    }
                ) {
                    Text("Go to End the trip", color = Color.White)
                }

        }
    }
    SnackbarHost(hostState = snackbarHostState)
}

@Composable
fun PreCheckItem(ichecked:Boolean,imageRes: DrawableResource, name: String, onClick: () -> Unit) {


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            onClick()
        }
    ) {
        Image(
            painter = painterResource(imageRes),
            contentDescription = name,
            modifier = Modifier
                .size(120.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                .padding(2.dp),
            contentScale = ContentScale.Fit
        )

        Text(
            text = name,
            fontSize = 16.sp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Check",
            tint = if (ichecked) Color(3, 17, 79, 255) else Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}
