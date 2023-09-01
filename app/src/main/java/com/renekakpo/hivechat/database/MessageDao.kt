package com.renekakpo.hivechat.database

import androidx.room.*
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.utils.Constants.MESSAGE_TB_NAME
import com.renekakpo.hivechat.utils.Constants.UPDATE_MESSAGE
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: Message): Long

    @Query("SELECT * FROM $MESSAGE_TB_NAME WHERE id = :id")
    fun read(id: Long): Message?

    @Query("SELECT * FROM $MESSAGE_TB_NAME WHERE chatId = :chatId")
    fun readAll(chatId: String): Flow<List<Message>>

    @Query("SELECT * FROM $MESSAGE_TB_NAME WHERE chatId = :chatId")
    fun getAllMessages(chatId: String): List<Message>

    @Query("SELECT * FROM $MESSAGE_TB_NAME WHERE chatId = :chatId ORDER BY deliveredTime DESC LIMIT 1")
    fun getLastMessage(chatId: String): Message?

    @Update
    suspend fun update(message: Message)

    @Query("UPDATE $MESSAGE_TB_NAME SET sentTime = :sentTime WHERE id = :messageId")
    suspend fun updateSentTime(messageId: Long, sentTime: Long)

    @Query("UPDATE $MESSAGE_TB_NAME SET deliveredTime = :deliveredTime, action = :newAction WHERE id = :messageId")
    suspend fun updateDeliveredTime(
        messageId: Long,
        deliveredTime: Long,
        newAction: String = UPDATE_MESSAGE
    )

    @Query("DELETE FROM $MESSAGE_TB_NAME WHERE id = :id")
    suspend fun delete(id: Long)

    suspend fun getUnreadMessageCount(chatId: String, senderUUID: String): Int {
        val messages = getAllMessages(chatId)
        return messages.count { it.isRead.not() && it.sender.uniqueIdentifier != senderUUID }
    }
}