package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.MailvoraUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
  state: MailvoraUiState,
  onUpdateSettings: (Boolean?, Int, Boolean, String) -> Unit,
  onNavigateToPrivacy: () -> Unit
) {
  var baseUrlInput by remember { mutableStateOf(state.customBaseUrl) }
  var notifications by remember { mutableStateOf(state.notificationsEnabled) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Settings", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "APPEARANCE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Theme Mode", style = MaterialTheme.typography.bodyLarge)
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              FilterChip(
                selected = state.isDarkTheme == null,
                onClick = { onUpdateSettings(null, state.refreshIntervalSeconds, notifications, baseUrlInput) },
                label = { Text("System") }
              )
              FilterChip(
                selected = state.isDarkTheme == false,
                onClick = { onUpdateSettings(false, state.refreshIntervalSeconds, notifications, baseUrlInput) },
                label = { Text("Light") }
              )
              FilterChip(
                selected = state.isDarkTheme == true,
                onClick = { onUpdateSettings(true, state.refreshIntervalSeconds, notifications, baseUrlInput) },
                label = { Text("Dark") }
              )
            }
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "INBOX & BACKEND", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text("Push Notifications", style = MaterialTheme.typography.bodyLarge)
                Text("Notify when new email arrives", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              }
              Switch(
                checked = notifications,
                onCheckedChange = {
                  notifications = it
                  onUpdateSettings(state.isDarkTheme, state.refreshIntervalSeconds, it, baseUrlInput)
                }
              )
            }

            HorizontalDivider()

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text("API Base URL", style = MaterialTheme.typography.bodyLarge)
              OutlinedTextField(
                value = baseUrlInput,
                onValueChange = { baseUrlInput = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
              )
              Button(
                onClick = {
                  onUpdateSettings(state.isDarkTheme, state.refreshIntervalSeconds, notifications, baseUrlInput)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Save Backend URL")
              }
            }
          }
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(text = "PRIVACY & ABOUT", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Bold)
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TextButton(
              onClick = onNavigateToPrivacy,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Privacy Center & Guidelines", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
              }
            }
            HorizontalDivider()
            Column(modifier = Modifier.padding(vertical = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
              Text("Mailvora", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
              Text("Owner: Ayush Yadav", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
              Text("Version 1.0.0 (Production)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text("Tagline: Private. Temporary. Simple.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyCenterScreen(onBack: () -> Unit) {
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Privacy Center", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .verticalScroll(rememberScrollState())
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "About Mailvora Privacy",
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Mailvora is designed to protect your personal identity from spam, phishing, and unwanted tracking by providing fully disposable temporary email addresses.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text("Important Guidelines:", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
          Text("• Temporary mailboxes and messages automatically expire.", style = MaterialTheme.typography.bodySmall)
          Text("• Do not use temporary email for banking, password recovery, or critical personal accounts.", style = MaterialTheme.typography.bodySmall)
          Text("• The service does not guarantee permanent data retention.", style = MaterialTheme.typography.bodySmall)
        }
      }
    }
  }
}
