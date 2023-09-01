package com.renekakpo.hivechat.models.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.renekakpo.hivechat.models.Message

class MessageTypeConverters {
    /**
     * Convert [Message] type to a Json string
     */
    @TypeConverter
    fun fromTypeToJson(message: Message): String = Gson().toJson(message)

    /**
     * Convert a JSON string to [Message] type
     */
    @TypeConverter
    fun fromJsonToType(data: String): Message = Gson().fromJson(data, Message::class.java)
}