package com.example.timelytrack.ui.navigation


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.timelytrack.ui.history.HistoryScreen
import com.example.timelytrack.ui.home.HomeScreen
import com.example.timelytrack.ui.profile.ProfileScreen

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigationHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = "home", modifier) {
        composable("home") { HomeScreen() }
        composable("history") { HistoryScreen() }
        composable("profile") { ProfileScreen() }
    }
}