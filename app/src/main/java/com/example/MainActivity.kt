package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.MailvoraNavGraph
import com.example.ui.theme.MailvoraTheme
import com.example.viewmodel.MailvoraViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MailvoraViewModel = viewModel()
      val state by viewModel.uiState.collectAsStateWithLifecycle()
      val darkTheme = state.isDarkTheme ?: isSystemInDarkTheme()

      MailvoraTheme(darkTheme = darkTheme) {
        MailvoraNavGraph(viewModel = viewModel)
      }
    }
  }
}
