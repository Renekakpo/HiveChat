package com.renekakpo.hivechat.models.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.renekakpo.hivechat.models.User

class ListOfUserConverters {
    /**
     * Convert a list of [User] to a JSON string
     */
    @TypeConverter
    fun fromTypeToJson(users: List<User>): String = Gson().toJson(users)

    /**
     * Convert a JSON string to a list of [User] object
     */
    @TypeConverter
    fun fromJsonToType(data: String): List<User> {
        val tokenType = object : TypeToken<List<User>>() {}.type
        return Gson().fromJson(data, tokenType) ?: listOf()
    }
}