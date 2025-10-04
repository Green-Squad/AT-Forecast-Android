package com.gsnamespace.atforecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.gsnamespace.atforecast.navigation.AppNavigation
import com.gsnamespace.atforecast.ui.theme.CallBudyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallBudyTheme {
                AppNavigation()
            }
        }
    }
}
