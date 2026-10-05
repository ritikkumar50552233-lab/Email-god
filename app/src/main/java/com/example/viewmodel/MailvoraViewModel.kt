package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Domain
import com.example.data.model.EmailMessage
import com.example.data.model.Mailbox
import com.example.data.repository.EmailRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MailvoraUiState(
  val currentMailbox: Mailbox? = null,
  val messages: List<EmailMessage> = emptyList(),
  val domains: List<Domain> = emptyList(),
  val history: List<Mailbox> = emptyList(),
  val isLoading: Boolean = false,
  val isCreatingMailbox: Boolean = false,
  val creationStep: String = "",
  val error: String? = null,
  val searchQuery: String = "",
  val filterType: String = "All",
  val isDarkTheme: Boolean? = null,
  val refreshIntervalSeconds: Int = 15,
  val notificationsEnabled: Boolean = true,
  val customBaseUrl: String = "https://api.mailvora.example/"
)

class MailvoraViewModel(application: Application) : AndroidViewModel(application) {
  private val repository = EmailRepository(application)

  private val _uiState = MutableStateFlow(MailvoraUiState())
  val uiState: StateFlow<MailvoraUiState> = _uiState.asStateFlow()

  init {
    loadDomains()
    loadHistory()
  }

  fun loadDomains() {
    viewModelScope.launch {
      val result = repository.getDomains()
      result.onSuccess { domains ->
        _uiState.update { it.copy(domains = domains) }
      }
    }
  }

  fun loadHistory() {
    viewModelScope.launch {
      val history = repository.getLocalHistory()
      val current = history.firstOrNull()
      _uiState.update { state ->
        state.copy(
          history = history,
          currentMailbox = state.currentMailbox ?: current
        )
      }
      if (_uiState.value.currentMailbox != null) {
        refreshMessages()
      } else {
        createMailbox()
      }
    }
  }

  fun createMailbox(username: String? = null, domain: String? = null) {
    viewModelScope.launch {
      _uiState.update { it.copy(isCreatingMailbox = true, creationStep = "Creating mailbox...", error = null) }
      kotlinx.coroutines.delay(200)
      _uiState.update { it.copy(creationStep = "Securing session...") }
      kotlinx.coroutines.delay(200)
      _uiState.update { it.copy(creationStep = "Preparing inbox...") }
      kotlinx.coroutines.delay(200)

      val result = repository.createMailbox(username, domain)
      result.onSuccess { box ->
        _uiState.update { state ->
          state.copy(
            currentMailbox = box,
            messages = emptyList(),
            isCreatingMailbox = false,
            creationStep = "",
            history = listOf(box) + state.history.filter { it.id != box.id }
          )
        }
      }.onFailure { err ->
        _uiState.update { it.copy(isCreatingMailbox = false, creationStep = "", error = err.message ?: "Failed to create mailbox") }
      }
    }
  }

  fun refreshMessages() {
    val box = _uiState.value.currentMailbox ?: return
    viewModelScope.launch {
      _uiState.update { it.copy(isLoading = true, error = null) }
      val result = repository.getMessages(box.id, box.token)
      result.onSuccess { msgs ->
        _uiState.update { it.copy(messages = msgs, isLoading = false) }
      }.onFailure { err ->
        _uiState.update { it.copy(isLoading = false, error = err.message) }
      }
    }
  }

  fun selectMailbox(mailbox: Mailbox) {
    _uiState.update { it.copy(currentMailbox = mailbox, messages = emptyList()) }
    refreshMessages()
  }

  fun deleteCurrentMailbox() {
    val box = _uiState.value.currentMailbox ?: return
    viewModelScope.launch {
      repository.deleteMailbox(box.id, box.token)
      val history = repository.getLocalHistory()
      val nextBox = history.firstOrNull()
      _uiState.update { it.copy(currentMailbox = nextBox, messages = emptyList(), history = history) }
      if (nextBox != null) {
        refreshMessages()
      }
    }
  }

  fun deleteHistoryItem(id: String) {
    viewModelScope.launch {
      repository.deleteHistoryItem(id)
      val history = repository.getLocalHistory()
      _uiState.update { it.copy(history = history) }
      if (_uiState.value.currentMailbox?.id == id) {
        val next = history.firstOrNull()
        _uiState.update { it.copy(currentMailbox = next, messages = emptyList()) }
        if (next != null) refreshMessages()
      }
    }
  }

  fun clearHistory() {
    viewModelScope.launch {
      repository.clearHistory()
      _uiState.update { it.copy(history = emptyList(), currentMailbox = null, messages = emptyList()) }
    }
  }

  fun setSearchQuery(query: String) {
    _uiState.update { it.copy(searchQuery = query) }
  }

  fun setFilterType(type: String) {
    _uiState.update { it.copy(filterType = type) }
  }

  val filteredMessages: List<EmailMessage>
    get() {
      val state = _uiState.value
      val query = state.searchQuery.lowercase()
      return state.messages.filter { msg ->
        val matchesQuery = query.isEmpty() ||
          msg.subject.lowercase().contains(query) ||
          msg.from.lowercase().contains(query) ||
          msg.preview.lowercase().contains(query)
        val matchesFilter = when (state.filterType) {
          "Unread" -> !msg.isRead
          "Read" -> msg.isRead
          else -> true
        }
        matchesQuery && matchesFilter
      }
    }

  fun updateSettings(
    darkTheme: Boolean?,
    refreshInterval: Int,
    notifications: Boolean,
    baseUrl: String
  ) {
    viewModelScope.launch {
      if (baseUrl != _uiState.value.customBaseUrl) {
        repository.updateBaseUrl(baseUrl)
      }
      _uiState.update {
        it.copy(
          isDarkTheme = darkTheme,
          refreshIntervalSeconds = refreshInterval,
          notificationsEnabled = notifications,
          customBaseUrl = baseUrl
        )
      }
    }
  }
}
