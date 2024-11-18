package com.example.timelytrack.ui.navigation
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController


class NavigationViewModel : ViewModel() {
    var currentRoute = mutableStateOf("home")
        private set

    fun onNavigationItemClicked(route: String, navController: NavController) {
        if (route != currentRoute.value) {
            currentRoute.value = route
            navController.navigate(route) {
                launchSingleTop = true
                restoreState = true
                popUpTo(navController.graph.startDestinationId) { saveState = true }
            }
        }
    }

    // Function to determine if back navigation is possible
    fun canNavigateBack(navController: NavController): Boolean {
        return navController.previousBackStackEntry != null
    }
}