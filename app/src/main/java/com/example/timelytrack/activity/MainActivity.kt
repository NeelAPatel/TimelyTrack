package com.example.timelytrack.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.timelytrack.TimelyTrackApplication
import com.example.timelytrack.ui.components.BottomNavBar
import com.example.timelytrack.viewmodel.LogEntryViewModelFactory
import com.example.timelytrack.viewmodel.LogViewModel2

//import com.example.timelytrack.viewmodel.LogViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
//                    val logViewModel: LogViewModel2 = viewModel(
//                        factory = LogEntryViewModelFactory((application as TimelyTrackApplication).repository)
//                    )
//                    val logViewModel: LogViewModel2 = viewModel()
                    val navController = rememberNavController()
                    BottomNavBar(navController = navController)
                }
            }
        }
    }
}
