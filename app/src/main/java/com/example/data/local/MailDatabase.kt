package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "mailbox_history")
data class MailboxEntity(
  @PrimaryKey val id: String,
  val email: String,
  val token: String?,
  val createdAt: Long,
  val expiresAt: Long,
  val status: String
)

@Dao
interface MailHistoryDao {
  @Query("SELECT * FROM mailbox_history ORDER BY createdAt DESC")
  suspend fun getAllHistory(): List<MailboxEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMailbox(mailbox: MailboxEntity)

  @Query("DELETE FROM mailbox_history WHERE id = :id")
  suspend fun deleteMailbox(id: String)

  @Query("DELETE FROM mailbox_history")
  suspend fun clearAll()
}

@Database(entities = [MailboxEntity::class], version = 1, exportSchema = false)
abstract class MailDatabase : RoomDatabase() {
  abstract fun mailHistoryDao(): MailHistoryDao

  companion object {
    @Volatile
    private var INSTANCE: MailDatabase? = null

    fun getDatabase(context: Context): MailDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          MailDatabase::class.java,
          "mailvora_database"
        ).fallbackToDestructiveMigration().build()
        INSTANCE = instance
        instance
      }
    }
  }
}
