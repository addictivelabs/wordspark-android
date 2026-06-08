package com.wordspark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wordspark.app.navigation.WordSparkNavHost
import com.wordspark.app.ui.theme.WordSparkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WordSparkTheme {
                WordSparkNavHost()
            }
        }
    }
}
