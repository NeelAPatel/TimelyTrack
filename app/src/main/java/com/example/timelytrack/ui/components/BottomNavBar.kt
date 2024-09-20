package com.example.timelytrack.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timelytrack.ui.history.HistoryScreen
import com.example.timelytrack.ui.home.HomeScreen
import com.example.timelytrack.ui.profile.ProfileScreen
import com.example.timelytrack.viewmodel.LogViewModel

@Preview(showBackground = true)
@Composable
fun BottomNavigationBarPreview() {
    val navController = rememberNavController()
    BottomBarComponent(navController)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNavBar( logViewModel: LogViewModel, navController: NavHostController = rememberNavController()
) {
    Scaffold(
        bottomBar = {
            BottomBarComponent(navController = navController)
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = "home", modifier = Modifier.padding(innerPadding)) {
            composable("home") { HomeScreen(viewModel = logViewModel) }
            composable("history") { HistoryScreen() }
            composable("profile") { ProfileScreen() }
        }
    }
}




@Composable
fun BottomBarComponent(navController: NavHostController) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                    restoreState = true
                }
            })


        NavigationBarItem(
            icon = { Icon(Icons.Filled.History, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "history",
            onClick = {
                navController.navigate("history") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        // ... (rest of your NavigationBarItems)
    }
}