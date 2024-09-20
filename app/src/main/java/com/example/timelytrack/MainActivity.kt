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
