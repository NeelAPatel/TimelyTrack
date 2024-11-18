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
import com.example.timelytrack.ui.navigation.NavigationViewModel
import com.example.timelytrack.ui.profile.ProfileScreen

@Composable
fun BottomNavBar(
    navController: NavHostController,
    navigationViewModel: NavigationViewModel
) {
    NavigationBar {
        val currentRoute by navigationViewModel.currentRoute

        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = { navigationViewModel.onNavigationItemClicked("home", navController) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.History, contentDescription = "History") },
            label = { Text("History") },
            selected = currentRoute == "history",
            onClick = { navigationViewModel.onNavigationItemClicked("history", navController) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = { navigationViewModel.onNavigationItemClicked("profile", navController) }
        )
    }
}
