package com.example.proyecto

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.graphics.Color
import com.example.proyecto.ui.FinanceApp
import com.example.proyecto.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(color = Color.White) {
                    val viewModel = FinanceViewModel(this@MainActivity)
                    FinanceApp(viewModel)
                }
            }
        }
    }
}
