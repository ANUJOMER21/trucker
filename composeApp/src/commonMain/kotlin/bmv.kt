
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.lighthousegames.logging.logging

@Composable
fun BottomNavigationBar(driverid: String,BacktoLogin:()->Unit) {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Home", "History", "Profile", "Logout")
    val log = logging("BottomNavigationBar Tag")
    log.d { driverid }
    Scaffold(
        bottomBar = {
            BottomNavigation(
                backgroundColor = Color(0, 18, 68, 255),
                elevation = 8.dp,
                modifier = Modifier
                    .padding(5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0, 18, 68, 255))

            ) {
                items.forEachIndexed { index, item ->
                    BottomNavigationItem(
                        icon = {
                            when (item) {
                                "Home" -> Icon(Icons.Default.Home, contentDescription = item,tint=Color.White)
                                "History" -> Icon(Icons.Default.History, contentDescription = item,tint=Color.White)
                                "Profile" -> Icon(Icons.Default.Person, contentDescription = item,tint=Color.White)
                                "Logout" -> Icon(Icons.Default.ExitToApp, contentDescription = item,tint=Color.White)
                            }
                        },
                        label = { Text(item, color = Color.White)},
                        selected = selectedItem == index,
                        onClick = { selectedItem = index }
                    )
                }
            }
        },
        backgroundColor =          Color(252,163,17,255),
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedItem) {
                0 -> HomeScreen(driverid)
                1 -> HistoryScreen(driverid)
                2 -> ProfileScreen(driverid)
                3 -> BacktoLogin()
            }
        }
    }
}

@Composable
fun HomeScreen(driverid: String) {
    // Your Home screen content
    NavPrecheck(driverid)
}

@Composable
fun HistoryScreen(driverid: String) {
    // Your History screen content
    History(driverid)
}

@Composable
fun ProfileScreen(driverid: String) {
    // Your Profile screen content
    Profile(driverid)

}

@Composable
fun LogoutScreen(
 ) {
    // Your Logout screen content


}
