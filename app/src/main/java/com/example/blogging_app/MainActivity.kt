package com.example.blogging_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.blogging_app.Screens.*
import com.example.blogging_app.ui.theme.BloggingAppTheme
import androidx.navigation.compose.composable
import com.example.blogging_app.navigation.NavGraph
import com.example.blogging_app.navigation.Routes

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloggingAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController= rememberNavController()
                    NavGraph(NavController = navController)
                }
            }
        }
    }
}
