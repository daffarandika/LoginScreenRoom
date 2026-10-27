package com.itsrobocon.loginscreenroom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.itsrobocon.loginscreenroom.ui.navigation.NavGraph
import com.itsrobocon.loginscreenroom.ui.theme.LoginScreenRoomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LoginScreenRoomTheme {
                NavGraph()
            }
        }
    }
}
