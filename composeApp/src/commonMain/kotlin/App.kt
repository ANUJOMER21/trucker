import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.jetbrains.compose.resources.painterResource
import org.lighthousegames.logging.logging

import trucker.composeapp.generated.resources.Res
import trucker.composeapp.generated.resources.truck2

@Composable
fun App(driver:(driverId:String)->Unit) {
    MaterialTheme {



        // Creating UserDao instance and passing in other screens

        val log = logging("userList")


        val navController = rememberNavController()
        AppNavigation(navController = navController){
driver(it)
        }
    }
}
fun NavHostController.navigateToPrecheck(driverId: String) {
    val options = NavOptions.Builder()
        .setPopUpTo(Screens.Login, inclusive = true) // Remove Login screen from back stack
        .build()

    this.navigate("${Screens.Precheck}?driver=$driverId", options)
}
@Composable
fun AppNavigation(navController: NavHostController,driver:(driverid:String)->Unit) {
    val driverid by remember { mutableStateOf("0") }
    driver(driverid)
    NavHost(navController = navController, startDestination = Screens.Login) {
        composable(route = Screens.Login) {
            LoginScreen(navController = navController)
        }
        composable(route =Screens.imagepick){
            imagepick()
        }

        composable(
            route = Screens.Precheck + "?driver={driver}",
            arguments = listOf(navArgument("driver") { type = NavType.StringType })
        ) { backStackEntry ->
            val driver = backStackEntry.arguments?.getString("driver")
            driver(driver!!)
            Precheck(driver, navController)
        }

        composable(
            route = Screens.TruckPrecheck + "?driver={driver}",
            arguments = listOf(navArgument("driver") { type = NavType.StringType })
        ) { backStackEntry ->
            val driver = backStackEntry.arguments?.getString("driver")
            TruckPrecheck(driver, navController)
        }

        composable(
            route = Screens.TrailerPrecheck + "?driver={driver}",
            arguments = listOf(navArgument("driver") { type = NavType.StringType })
        ) { backStackEntry ->
            val driver = backStackEntry.arguments?.getString("driver")
            TrailerPrecheck(driver, navController)
        }

        composable(route = Screens.meter+ "?driver={driver}",
                arguments = listOf(navArgument("driver") { type = NavType.StringType }) )
        {
                        backStackEntry ->
                    val driver = backStackEntry.arguments?.getString("driver")
            meter(driver,navController)
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
            .height(350.dp)
            .padding(top = 60.dp)
    )
}





object Screens {
    val imagepick="Imagepick"
    const val Login = "login"
    const val Precheck = "precheck"
    const val TruckPrecheck = "truckprecheck"
    const val TrailerPrecheck = "trailerprecheck"
    const val meter = "meter"
}
