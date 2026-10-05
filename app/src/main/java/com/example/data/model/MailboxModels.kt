package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Mailbox(
  @Json(name = "id") val id: String,
  @Json(name = "email") val email: String,
  @Json(name = "token") val token: String? = null,
  @Json(name = "createdAt") val createdAt: Long = System.currentTimeMillis(),
  @Json(name = "expiresAt") val expiresAt: Long = System.currentTimeMillis() + 3600 * 1000,
  @Json(name = "status") val status: String = "ACTIVE"
)

@JsonClass(generateAdapter = true)
data class EmailMessage(
  @Json(name = "id") val id: String,
  @Json(name = "from") val from: String,
  @Json(name = "subject") val subject: String,
  @Json(name = "body") val body: String,
  @Json(name = "preview") val preview: String,
  @Json(name = "receivedAt") val receivedAt: Long = System.currentTimeMillis(),
  @Json(name = "isRead") val isRead: Boolean = false,
  @Json(name = "to") val to: String = "",
  @Json(name = "attachments") val attachments: List<Attachment> = emptyList()
)

@JsonClass(generateAdapter = true)
data class Attachment(
  @Json(name = "id") val id: String,
  @Json(name = "filename") val filename: String,
  @Json(name = "fileType") val fileType: String,
  @Json(name = "fileSize") val fileSize: Long,
  @Json(name = "url") val url: String? = null
)

@JsonClass(generateAdapter = true)
data class Domain(
  @Json(name = "id") val id: String,
  @Json(name = "domain") val domain: String
)

sealed class ApiError(val message: String) {
  class NetworkError(msg: String = "No internet connection") : ApiError(msg)
  class TimeoutError(msg: String = "Connection timeout") : ApiError(msg)
  class RateLimitError(msg: String = "Rate limit exceeded. Please wait.") : ApiError(msg)
  class ServerError(msg: String = "Server unavailable") : ApiError(msg)
  class MailboxExpiredError(msg: String = "Mailbox has expired") : ApiError(msg)
  class ServiceUnconfigured(msg: String = "Mail service not configured. Connect a real Mailvora backend.") : ApiError(msg)
  class UnknownError(msg: String = "An unexpected error occurred") : ApiError(msg)
}
