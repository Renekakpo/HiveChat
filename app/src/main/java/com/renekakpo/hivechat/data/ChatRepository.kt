package com.renekakpo.hivechat.data

import com.renekakpo.hivechat.database.ChatDao
import com.renekakpo.hivechat.models.Chat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun insertChat(chat: Chat)

    fun readChatStream(itemUuid: String): Chat?

    fun readAllChatStream(): Flow<List<Chat>>

    suspend fun updateChat(chat: Chat)

    suspend fun deleteChat(id: Long)
}

class ChatReposImp(private val chatDao: ChatDao): ChatRepository {
    override suspend fun insertChat(chat: Chat) = chatDao.insert(chat)

    override fun readChatStream(itemUuid: String): Chat = chatDao.read(itemUuid)

    override fun readAllChatStream(): Flow<List<Chat>> = chatDao.readAll()

    override suspend fun updateChat(chat: Chat) = chatDao.update(chat)

    override suspend fun deleteChat(id: Long) = chatDao.delete(id)
}