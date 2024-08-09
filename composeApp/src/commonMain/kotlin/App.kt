import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.KmLogging
import org.lighthousegames.logging.logging
import trucker.composeapp.generated.resources.Res
import trucker.composeapp.generated.resources.truck2

@Composable
fun App() {
    MaterialTheme {


    }
}





fun NavGraphBuilder.composableWithDriver(
    route: String,
    onDriverReceived: @Composable (driverId: String) -> Unit
) {
    composable(
        route = route,
        arguments = listOf(navArgument("driver") { type = NavType.StringType })
    ) { backStackEntry ->
        val driver = backStackEntry.arguments?.getString("driver")
        val log = logging("composableWithDriver Tag")
        log.d { driver }
        if (driver != null) {
            onDriverReceived(driver)
        } else {
            // Handle the case where driver is null
            onDriverReceived("0")
            // Optionally navigate to an error screen or show a fallback UI
        }
    }
}


@Composable
fun DeliveryImage() {
    Image(
        painter = painterResource(Res.drawable.truck2),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(top = 60.dp)
    )
}

object Screens {

    const val Login = "login"
    const val Precheck = "precheck"
    const val TruckPrecheck = "truckprecheck"
    const val TrailerPrecheck = "trailerprecheck"
    const val meter = "meter"
    const val bmv = "bmv"
}
