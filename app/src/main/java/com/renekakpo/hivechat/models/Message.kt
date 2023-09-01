package com.renekakpo.hivechat.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.renekakpo.hivechat.models.converters.UserTypeConverters
import com.renekakpo.hivechat.utils.Constants.MESSAGE_TB_NAME
import com.renekakpo.hivechat.utils.Constants.NEW_MESSAGE

@Entity(tableName = MESSAGE_TB_NAME, indices = [Index(value = ["id", "timestamp"], unique = true)])
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String = NEW_MESSAGE,
    val chatId: String,
    @TypeConverters(UserTypeConverters::class)
    val sender: User,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sentTime: Long?,
    val deliveredTime: Long?,
    val readTime: Long?,
    val isRead: Boolean,
)