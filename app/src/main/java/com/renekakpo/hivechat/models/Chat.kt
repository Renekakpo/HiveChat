package com.renekakpo.hivechat.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.renekakpo.hivechat.models.converters.ListOfUserConverters
import com.renekakpo.hivechat.utils.Constants.CHAT_TB_NAME

@Entity(
    tableName = CHAT_TB_NAME,
    indices = [Index(value = ["uniqueIdentifier"], unique = true)]
)
data class Chat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uniqueIdentifier: String,
    val type: String,
    val name: String?,
    val description: String?,
    val profilePictureUrl: String?,
    @TypeConverters(ListOfUserConverters::class)
    val participants: List<User>,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
