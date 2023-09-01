package com.renekakpo.hivechat.database

import androidx.room.*
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.utils.Constants.CHAT_TB_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chat: Chat)

    @Query("SELECT * FROM $CHAT_TB_NAME WHERE uniqueIdentifier = :itemUuid")
    fun read(itemUuid: String): Chat

    @Query("SELECT * FROM $CHAT_TB_NAME ORDER BY updatedAt DESC")
    fun readAll(): Flow<List<Chat>>

    @Query("SELECT uniqueIdentifier FROM $CHAT_TB_NAME")
    fun readAllUniqueIdentifiers(): Flow<List<String>>

    @Update
    suspend fun update(chat: Chat)

    @Query("DELETE FROM $CHAT_TB_NAME WHERE id = :id")
    suspend fun delete(id: Long)
}