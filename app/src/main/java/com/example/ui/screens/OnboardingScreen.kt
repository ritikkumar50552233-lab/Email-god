package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class OnboardingPage(
  val title: String,
  val subtitle: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
  val pages = listOf(
    OnboardingPage("Temporary email, simplified.", "Generate secure disposable addresses instantly without exposing your personal inbox.", Icons.Default.MarkEmailUnread),
    OnboardingPage("Zero spam. Total privacy.", "Receive emails safely, protect your identity from trackers, and maintain pristine digital hygiene.", Icons.Default.PrivacyTip),
    OnboardingPage("Automatic expiration.", "Your disposable mailboxes automatically expire and self-destruct when you no longer need them.", Icons.Default.Timer),
    OnboardingPage("You're ready.", "Experience the ultimate privacy-first temporary email client built by Ayush Yadav.", Icons.Default.MarkEmailUnread)
  )

  var pagerIndex by remember { mutableIntStateOf(0) }
  val page = pages[pagerIndex]

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 32.dp),
      horizontalArrangement = Arrangement.End
    ) {
      if (pagerIndex < pages.size - 1) {
        OutlinedButton(
          onClick = { onFinished() },
          shape = RoundedCornerShape(20.dp)
        ) {
          Text("Skip", color = MaterialTheme.colorScheme.onBackground)
        }
      }
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(horizontal = 16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(110.dp)
          .background(
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            shape = RoundedCornerShape(36.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = page.icon,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(56.dp)
        )
      }
      Spacer(modifier = Modifier.height(36.dp))
      Text(
        text = page.title,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onBackground
      )
      Spacer(modifier = Modifier.height(16.dp))
      Text(
        text = page.subtitle,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(bottom = 32.dp)
    ) {
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 24.dp)
      ) {
        pages.forEachIndexed { index, _ ->
          Box(
            modifier = Modifier
              .size(if (index == pagerIndex) 24.dp else 8.dp, 8.dp)
              .background(
                color = if (index == pagerIndex) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                shape = CircleShape
              )
          )
        }
      }

      Button(
        onClick = {
          if (pagerIndex < pages.size - 1) {
            pagerIndex++
          } else {
            onFinished()
          }
        },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
      ) {
        Text(
          text = if (pagerIndex == pages.size - 1) "Get Started" else "Continue",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        if (pagerIndex < pages.size - 1) {
          Spacer(modifier = Modifier.size(8.dp))
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }
    }
  }
}
