package com.example.timelytrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.timelytrack.ui.components.BottomNavBar
import com.example.timelytrack.viewmodel.LogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
//            val windowSizeClass = calculateWindowSizeClass(this)
            MaterialTheme {
                Surface {
                    val logViewModel: LogViewModel = viewModel()
                    val navController = rememberNavController()
                    BottomNavBar(navController = navController, logViewModel = logViewModel  /*, windowSizeClass = windowSizeClass*/)
                }
            }
        }
    }
}
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            TimelyTrackTheme {
//                MyApp()
//            }
//        }
//    }
//}
//
//@Composable
//fun MyApp() {
//
//    //Navigation Controller
//    val navController  = rememberNavController()
//
//    Scaffold (
//        bottomBar = { BottomNavigationBar(navController) },
//        floatingActionButton = {
//            val navBackStackEntry by navController.currentBackStackEntryAsState()
//            val currentRoute = navBackStackEntry?.destination?.route
//
//            if (currentRoute == "home") {
//                Column(
//                    horizontalAlignment = Alignment.End,
//                    verticalArrangement = Arrangement.spacedBy(8.dp),
//                    modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
//                )  {
//                    SmallFAB(
//                        contentDescription = "Complete",  //checkmark
//                        myIcon = Icons.Filled.Check
//                    )
//                    ExtendedFAB(
//                        text = "New Log",
//                        contentDescription = "New Log",
//                        myIcon = Icons.Filled.Add
//                    )
//                }
//            }
//        },
//
//        // maybe listview in topbar scaffolding = bad idea.. need to move it to "homescreen"
//        content = { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = "home",
//                modifier = Modifier.padding(innerPadding)
//            ) {
//                composable("home") { HomeScreen() }
//            }
//        }
//    )
//
//}
//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//
//    NavigationBar {
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_home),
//                    contentDescription = "Home"
//                )
//            },
//            label = { Text("Home") },
//            selected = currentRoute == "home",
//            onClick = {
//                if (currentRoute != "home") {
//                    navController.navigate("home") {
//                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            }
//        )
//
//        /*
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_tags),
//                    contentDescription = "Tags"
//                )
//            },
//            label = { Text("Tags") },
//            selected = currentRoute == "tags",
//            onClick = {
//                if (currentRoute != "tags") {
//                    navController.navigate("tags") {
//                        popUpTo(navController.graph.startDestinationId) {
//                            saveState = true
//                        }
//                        launchSingleTop = true
//                        restoreState = true
//                    }
//                }
//            }
//        )
//         */
//    }
//}
//
//@Composable // Generalized
//fun SmallFAB(contentDescription: String, myIcon: ImageVector) {
//    SmallFloatingActionButton(
//        onClick = { /* TODO: Implement Camera action */ },
//        containerColor = MaterialTheme.colorScheme.onPrimary,
//        contentColor = MaterialTheme.colorScheme.primary,
//        modifier = Modifier.size(48.dp)
//    ) {
//        Icon(myIcon, contentDescription)
//    }
//}
//
//@Composable // Generalized
//fun ExtendedFAB(contentDescription: String, text: String, myIcon: ImageVector) {
//    ExtendedFloatingActionButton(
//        onClick = { /* TODO: Implement Add action */ },
//        containerColor = MaterialTheme.colorScheme.primary,
//        contentColor = MaterialTheme.colorScheme.onPrimary,
//        icon = { Icon(myIcon, contentDescription) },
//        text = { Text(text) }
//    )
//}
//
//@Composable
//fun HomeScreen() {
//    // Example content for the Home screen
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(text = "Home Page")
//    }
//}
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    TimelyTrackTheme {
////        Greeting("Android")
//        MyApp()
//    }
//}