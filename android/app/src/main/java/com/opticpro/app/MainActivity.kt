package com.opticpro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.opticpro.app.ui.theme.OpticProTheme
import dagger.hilt.android.AndroidEntryPoint
import com.opticpro.app.ui.theme.OpticProTheme
import com.opticpro.app.ui.auth.LoginScreen
import com.opticpro.app.ui.navigation.AppNavHost
import com.opticpro.app.ui.theme.OpticProTheme
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OpticProTheme { AppNavHost() }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    OpticProTheme {
        Greeting("Android")
    }
}