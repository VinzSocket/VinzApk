package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AuthScreen
import com.example.ui.MainScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MainViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                if (uiState.isLoggedIn) {
                    MainScreen(
                        viewModel = viewModel,
                        onLogout = { viewModel.logout() }
                    )
                } else {
                    AuthScreen(
                        onLogin = { username -> viewModel.login(username) }
                    )
                }
            }
        }
    }
}
