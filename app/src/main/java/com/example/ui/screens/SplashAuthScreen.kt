package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashAuthScreen(onSplashFinished: () -> Unit) {
  var startAnimation by remember { mutableStateOf(false) }
  val alphaAnim = animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0f,
    animationSpec = tween(1200)
  )
  val scaleAnim = animateFloatAsState(
    targetValue = if (startAnimation) 1f else 0.8f,
    animationSpec = tween(1000)
  )

  LaunchedEffect(Unit) {
    startAnimation = true
    delay(2000)
    onSplashFinished()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            MaterialTheme.colorScheme.background,
            MaterialTheme.colorScheme.surfaceVariant
          )
        )
      ),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier
        .alpha(alphaAnim.value)
        .scale(scaleAnim.value)
    ) {
      Box(
        modifier = Modifier
          .size(96.dp)
          .background(
            MaterialTheme.colorScheme.primaryContainer,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(28.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MarkEmailUnread,
          contentDescription = "Mailvora Logo",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(52.dp)
        )
      }
      Spacer(modifier = Modifier.height(24.dp))
      Text(
        text = "MAILVORA",
        style = MaterialTheme.typography.headlineLarge,
        fontWeight = FontWeight.Bold,
        letterSpacing = 4.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "\"Private. Temporary. Simple.\"",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.secondary
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "Owner: Ayush Yadav",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
