package com.example.data.remote

import com.example.data.model.Domain
import com.example.data.model.EmailMessage
import com.example.data.model.Mailbox
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface MailvoraApi {
  @GET("domains")
  suspend fun getDomains(): Response<List<Domain>>

  @POST("mailbox")
  suspend fun createMailbox(@Body request: CreateMailboxRequest? = null): Response<Mailbox>

  @GET("mailbox/{id}/messages")
  suspend fun getMessages(
    @Path("id") mailboxId: String,
    @Header("Authorization") token: String? = null
  ): Response<List<EmailMessage>>

  @GET("mailbox/{id}/messages/{messageId}")
  suspend fun getMessage(
    @Path("id") mailboxId: String,
    @Path("messageId") messageId: String,
    @Header("Authorization") token: String? = null
  ): Response<EmailMessage>

  @DELETE("mailbox/{id}")
  suspend fun deleteMailbox(
    @Path("id") mailboxId: String,
    @Header("Authorization") token: String? = null
  ): Response<Unit>

  @DELETE("mailbox/{id}/messages/{messageId}")
  suspend fun deleteMessage(
    @Path("id") mailboxId: String,
    @Path("messageId") messageId: String,
    @Header("Authorization") token: String? = null
  ): Response<Unit>
}

data class CreateMailboxRequest(
  val username: String? = null,
  val domain: String? = null
)
