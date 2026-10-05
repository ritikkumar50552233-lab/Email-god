package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.viewmodel.MailvoraUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateMailScreen(
  state: MailvoraUiState,
  onCreateCustomMailbox: (String, String) -> Unit,
  onCreateRandomMailbox: () -> Unit,
  onNavigateToInbox: () -> Unit
) {
  var customUsername by remember { mutableStateOf("") }
  var selectedDomain by remember { mutableStateOf(state.domains.firstOrNull()?.domain ?: "mailvora.io") }
  var expandedDomainDropdown by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Create New Mailbox", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      if (state.isCreatingMailbox) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center,
          modifier = Modifier.fillMaxWidth()
        ) {
          CircularProgressIndicator(modifier = Modifier.size(56.dp), strokeWidth = 4.dp)
          Spacer(modifier = Modifier.height(24.dp))
          Text(
            text = state.creationStep.ifEmpty { "Generating mailbox..." },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Setting up secure encrypted session",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
          )
        }
      } else {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(80.dp)
              .background(
                MaterialTheme.colorScheme.primaryContainer,
                RoundedCornerShape(24.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(40.dp)
            )
          }

          Text(
            text = "Generate Temporary Address",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
          )

          Text(
            text = "Create an instant disposable email address with a single tap or customize your prefix.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = customUsername,
            onValueChange = { customUsername = it },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("custom_username_input"),
            placeholder = { Text("Custom username (optional)") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
          )

          ExposedDropdownMenuBox(
            expanded = expandedDomainDropdown,
            onExpandedChange = { expandedDomainDropdown = !expandedDomainDropdown },
            modifier = Modifier.fillMaxWidth()
          ) {
            OutlinedTextField(
              value = selectedDomain,
              onValueChange = {},
              readOnly = true,
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
              label = { Text("Select Domain") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDomainDropdown) },
              shape = RoundedCornerShape(16.dp)
            )
            ExposedDropdownMenu(
              expanded = expandedDomainDropdown,
              onDismissRequest = { expandedDomainDropdown = false }
            ) {
              state.domains.forEach { domainItem ->
                DropdownMenuItem(
                  text = { Text(domainItem.domain) },
                  onClick = {
                    selectedDomain = domainItem.domain
                    expandedDomainDropdown = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = {
              onCreateCustomMailbox(customUsername, selectedDomain)
              onNavigateToInbox()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("create_custom_mailbox_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text("Create Custom Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              onCreateRandomMailbox()
              onNavigateToInbox()
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("create_random_mailbox_button"),
            shape = RoundedCornerShape(16.dp)
          ) {
            Text("Generate Random Address", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
