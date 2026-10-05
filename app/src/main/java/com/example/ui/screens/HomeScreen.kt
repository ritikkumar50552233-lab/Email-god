package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailMessage
import com.example.viewmodel.MailvoraUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  state: MailvoraUiState,
  onRefresh: () -> Unit,
  onDeleteMailbox: () -> Unit,
  onOpenMessage: (EmailMessage) -> Unit,
  onSearchChanged: (String) -> Unit,
  onFilterChanged: (String) -> Unit,
  filteredMessages: List<EmailMessage>
) {
  val clipboardManager: ClipboardManager = LocalClipboardManager.current
  val context = LocalContext.current
  var showDeleteDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text(
              text = "MAILVORA",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            )
            Text(
              text = "Private • Temporary • Simple",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.secondary
            )
          }
        },
        actions = {
          IconButton(
            onClick = onRefresh,
            modifier = Modifier.testTag("refresh_button")
          ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh Inbox")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
      )
    },
    containerColor = MaterialTheme.colorScheme.background
  ) { padding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(4.dp))
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("mailbox_card"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.linearGradient(
                  colors = listOf(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    MaterialTheme.colorScheme.surface
                  )
                )
              )
              .padding(20.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(10.dp)
                      .background(MaterialTheme.colorScheme.tertiary, CircleShape)
                  )
                  Spacer(modifier = Modifier.size(8.dp))
                  Text(
                    text = state.currentMailbox?.status ?: "ACTIVE",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                  )
                }
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = "Expires in ~58m",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "YOUR TEMPORARY ADDRESS",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 1.sp
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = state.currentMailbox?.email ?: "No active mailbox",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("email_address_text")
              )

              Spacer(modifier = Modifier.height(20.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Button(
                  onClick = {
                    state.currentMailbox?.email?.let {
                      clipboardManager.setText(AnnotatedString(it))
                    }
                  },
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("copy_email_button"),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                  Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Copy", style = MaterialTheme.typography.labelLarge)
                }

                OutlinedButton(
                  onClick = {
                    state.currentMailbox?.email?.let { email ->
                      val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "My temporary email address: $email")
                        type = "text/plain"
                      }
                      val shareIntent = Intent.createChooser(sendIntent, null)
                      context.startActivity(shareIntent)
                    }
                  },
                  modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .testTag("share_email_button"),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text("Share", style = MaterialTheme.typography.labelLarge)
                }

                IconButton(
                  onClick = { showDeleteDialog = true },
                  modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(12.dp))
                    .testTag("delete_mailbox_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Mailbox",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                  )
                }
              }
            }
          }
        }
      }

      item {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = state.searchQuery,
            onValueChange = onSearchChanged,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("search_input"),
            placeholder = { Text("Search sender or subject...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = MaterialTheme.colorScheme.primary,
              unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
            )
          )

          Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            listOf("All", "Unread", "Read").forEach { filter ->
              FilterChip(
                selected = state.filterType == filter,
                onClick = { onFilterChanged(filter) },
                label = { Text(filter) },
                shape = RoundedCornerShape(10.dp)
              )
            }
          }
        }
      }

      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Inbox Messages (${filteredMessages.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          if (state.isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
          }
        }
      }

      if (filteredMessages.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
              )
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "No messages yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Waiting for incoming email...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
              )
            }
          }
        }
      } else {
        items(filteredMessages, key = { it.id }) { msg ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(16.dp))
              .clickable { onOpenMessage(msg) }
              .testTag("message_item_${msg.id}"),
            colors = CardDefaults.cardColors(
              containerColor = if (!msg.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f) else MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = msg.from,
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (!msg.isRead) {
                  Box(
                    modifier = Modifier
                      .size(8.dp)
                      .background(MaterialTheme.colorScheme.primary, CircleShape)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = msg.subject.ifEmpty { "(No Subject)" },
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = msg.preview,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }
      }

      item {
        Spacer(modifier = Modifier.height(32.dp))
      }
    }
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      title = { Text("Delete Mailbox") },
      text = { Text("Are you sure you want to delete this temporary mailbox? This action cannot be undone.") },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDeleteMailbox()
          }
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}
