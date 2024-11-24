package com.example.timelytrack.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.example.timelytrack.ui.components.BottomNavBar
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timelytrack.ui.components.AppTopBar
import com.example.timelytrack.ui.navigation.NavigationHost
import com.example.timelytrack.ui.navigation.NavigationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val navigationViewModel: NavigationViewModel = viewModel()

                Scaffold(
                    topBar = {
                        AppTopBar(
                            canNavigateBack = navigationViewModel.canNavigateBack(navController),
                            onBackClick = { navController.popBackStack() }
                        )
                    },
                    bottomBar = {
                        BottomNavBar(navController = navController, navigationViewModel = navigationViewModel)
                    }
                ) { innerPadding ->
                    NavigationHost(navController, Modifier.padding(innerPadding))
                }
            }
            }
        }
}