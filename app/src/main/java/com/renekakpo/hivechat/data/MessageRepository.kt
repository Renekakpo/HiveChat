package com.renekakpo.hivechat.data

import com.renekakpo.hivechat.database.MessageDao
import com.renekakpo.hivechat.models.Message
import kotlinx.coroutines.flow.Flow

interface MessageRepository {
    suspend fun insertMessage(message: Message): Long

    fun readMessageStream(id: Long): Message?

    fun readAllMessageStream(chatId: String): Flow<List<Message>>

    suspend fun unreadCount(chatId: String, senderUUID: String): Int

    fun lastMessageDelivered(chatId: String): Message?

    suspend fun updateMessage(message: Message)

    suspend fun updateSentTime(messageId: Long, sentTime: Long)

    suspend fun updateDeliveredTime(messageId: Long, deliveredTime: Long, newAction: String)

    suspend fun deleteMessage(id: Long)
}

class MessageReposImp(private val messageDao: MessageDao) : MessageRepository {
    override suspend fun insertMessage(message: Message): Long = messageDao.insert(message)

    override fun readMessageStream(id: Long): Message? = messageDao.read(id)

    override fun readAllMessageStream(chatId: String): Flow<List<Message>> =
        messageDao.readAll(chatId)

    override suspend fun unreadCount(chatId: String, senderUUID: String): Int =
        messageDao.getUnreadMessageCount(chatId = chatId, senderUUID = senderUUID)

    override fun lastMessageDelivered(chatId: String): Message? = messageDao.getLastMessage(chatId)

    override suspend fun updateMessage(message: Message) = messageDao.update(message)

    override suspend fun updateSentTime(messageId: Long, sentTime: Long) =
        messageDao.updateSentTime(messageId, sentTime)

    override suspend fun updateDeliveredTime(messageId: Long, deliveredTime: Long, newAction: String) =
        messageDao.updateDeliveredTime(messageId, deliveredTime, newAction)

    override suspend fun deleteMessage(id: Long) = messageDao.delete(id)
}