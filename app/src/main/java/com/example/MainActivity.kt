package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.CalculatorScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.CalculatorViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          val calculatorViewModel: CalculatorViewModel = viewModel()
          CalculatorScreen(viewModel = calculatorViewModel)
        }
      }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
  MyApplicationTheme {
    val calculatorViewModel: CalculatorViewModel = viewModel()
    CalculatorScreen(viewModel = calculatorViewModel)
  }
}

