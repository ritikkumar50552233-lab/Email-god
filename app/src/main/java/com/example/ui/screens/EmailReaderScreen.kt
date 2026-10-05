package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.EmailMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailReaderScreen(
  message: EmailMessage?,
  onBack: () -> Unit,
  onDelete: (String) -> Unit
) {
  val clipboardManager: ClipboardManager = LocalClipboardManager.current
  val context = LocalContext.current

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Email Message", fontWeight = FontWeight.Bold) },
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("back_button")
          ) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        actions = {
          if (message != null) {
            IconButton(
              onClick = {
                clipboardManager.setText(AnnotatedString(message.body))
              }
            ) {
              Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy Body")
            }
            IconButton(
              onClick = {
                onDelete(message.id)
                onBack()
              }
            ) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Email", tint = MaterialTheme.colorScheme.error)
            }
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    if (message == null) {
      Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
        Text("Message not found or deleted.")
      }
    } else {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(padding)
          .verticalScroll(rememberScrollState())
          .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Text(
          text = message.subject.ifEmpty { "(No Subject)" },
          style = MaterialTheme.typography.headlineSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onBackground
        )

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
          Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row {
              Text(text = "From: ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = message.from, color = MaterialTheme.colorScheme.onSurface)
            }
            Row {
              Text(text = "To: ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = message.to.ifEmpty { "temporary-inbox@mailvora" }, color = MaterialTheme.colorScheme.onSurface)
            }
            Row {
              Text(text = "Received: ", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(message.receivedAt)), color = MaterialTheme.colorScheme.onSurface)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(modifier = Modifier.padding(20.dp)) {
            Text(
              text = message.body,
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        if (message.attachments.isNotEmpty()) {
          Text(text = "Attachments (${message.attachments.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
          message.attachments.forEach { att ->
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(12.dp)
            ) {
              Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(text = att.filename, fontWeight = FontWeight.SemiBold)
                  Text(text = "${att.fileSize / 1024} KB", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = {}) {
                  Text("Download")
                }
              }
            }
          }
        }
      }
    }
  }
}
