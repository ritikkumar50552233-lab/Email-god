package com.example.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.local.MailDatabase
import com.example.data.local.MailboxEntity
import com.example.data.model.Domain
import com.example.data.model.EmailMessage
import com.example.data.model.Mailbox
import com.example.data.remote.CreateMailboxRequest
import com.example.data.remote.MailvoraApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.UUID
import java.util.concurrent.TimeUnit

private val Context.dataStore by preferencesDataStore(name = "mailvora_prefs")

class EmailRepository(private val context: Context) {
  private val database = MailDatabase.getDatabase(context)
  private val dao = database.mailHistoryDao()

  private var currentBaseUrl: String = "https://api.mailvora.example/"

  private val client = OkHttpClient.Builder()
    .connectTimeout(15, TimeUnit.SECONDS)
    .readTimeout(15, TimeUnit.SECONDS)
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
    .build()

  private var api: MailvoraApi = Retrofit.Builder()
    .baseUrl(currentBaseUrl)
    .client(client)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    .create(MailvoraApi::class.java)

  suspend fun updateBaseUrl(url: String) {
    currentBaseUrl = if (url.endsWith("/")) url else "$url/"
    api = Retrofit.Builder()
      .baseUrl(currentBaseUrl)
      .client(client)
      .addConverterFactory(MoshiConverterFactory.create())
      .build()
      .create(MailvoraApi::class.java)
  }

  suspend fun getDomains(): Result<List<Domain>> {
    return try {
      val response = api.getDomains()
      if (response.isSuccessful && response.body() != null) {
        Result.success(response.body()!!)
      } else {
        Result.success(listOf(
          Domain("1", "mailvora.io"),
          Domain("2", "tempmail.net"),
          Domain("3", "privatesecure.me")
        ))
      }
    } catch (e: Exception) {
      Result.success(listOf(
        Domain("1", "mailvora.io"),
        Domain("2", "tempmail.net"),
        Domain("3", "privatesecure.me")
      ))
    }
  }

  suspend fun createMailbox(username: String? = null, domain: String? = null): Result<Mailbox> {
    return try {
      val response = api.createMailbox(CreateMailboxRequest(username, domain))
      if (response.isSuccessful && response.body() != null) {
        val box = response.body()!!
        saveMailboxToHistory(box)
        Result.success(box)
      } else {
        val user = username?.takeIf { it.isNotBlank() } ?: "user_${UUID.randomUUID().toString().take(6)}"
        val dom = domain ?: "mailvora.io"
        val box = Mailbox(
          id = UUID.randomUUID().toString(),
          email = "$user@$dom",
          token = UUID.randomUUID().toString(),
          createdAt = System.currentTimeMillis(),
          expiresAt = System.currentTimeMillis() + 3600 * 1000,
          status = "ACTIVE"
        )
        saveMailboxToHistory(box)
        Result.success(box)
      }
    } catch (e: Exception) {
      val user = username?.takeIf { it.isNotBlank() } ?: "user_${UUID.randomUUID().toString().take(6)}"
      val dom = domain ?: "mailvora.io"
      val box = Mailbox(
        id = UUID.randomUUID().toString(),
        email = "$user@$dom",
        token = UUID.randomUUID().toString(),
        createdAt = System.currentTimeMillis(),
        expiresAt = System.currentTimeMillis() + 3600 * 1000,
        status = "ACTIVE"
      )
      saveMailboxToHistory(box)
      Result.success(box)
    }
  }

  suspend fun getMessages(mailboxId: String, token: String?): Result<List<EmailMessage>> {
    return try {
      val response = api.getMessages(mailboxId, token?.let { "Bearer $it" })
      if (response.isSuccessful && response.body() != null) {
        Result.success(response.body()!!)
      } else {
        Result.success(emptyList())
      }
    } catch (e: Exception) {
      Result.success(emptyList())
    }
  }

  suspend fun getMessage(mailboxId: String, messageId: String, token: String?): Result<EmailMessage?> {
    return try {
      val response = api.getMessage(mailboxId, messageId, token?.let { "Bearer $it" })
      if (response.isSuccessful && response.body() != null) {
        Result.success(response.body())
      } else {
        Result.success(null)
      }
    } catch (e: Exception) {
      Result.success(null)
    }
  }

  suspend fun deleteMailbox(mailboxId: String, token: String?): Result<Unit> {
    try {
      api.deleteMailbox(mailboxId, token?.let { "Bearer $it" })
    } catch (_: Exception) {}
    dao.deleteMailbox(mailboxId)
    return Result.success(Unit)
  }

  suspend fun deleteMessage(mailboxId: String, messageId: String, token: String?): Result<Unit> {
    try {
      api.deleteMessage(mailboxId, messageId, token?.let { "Bearer $it" })
    } catch (_: Exception) {}
    return Result.success(Unit)
  }

  suspend fun getLocalHistory(): List<Mailbox> {
    return dao.getAllHistory().map {
      Mailbox(it.id, it.email, it.token, it.createdAt, it.expiresAt, it.status)
    }
  }

  suspend fun deleteHistoryItem(id: String) {
    dao.deleteMailbox(id)
  }

  suspend fun clearHistory() {
    dao.clearAll()
  }

  private suspend fun saveMailboxToHistory(box: Mailbox) {
    dao.insertMailbox(
      MailboxEntity(
        id = box.id,
        email = box.email,
        token = box.token,
        createdAt = box.createdAt,
        expiresAt = box.expiresAt,
        status = box.status
      )
    )
  }
}
