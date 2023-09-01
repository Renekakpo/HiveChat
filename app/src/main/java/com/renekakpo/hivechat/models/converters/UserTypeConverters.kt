package com.renekakpo.hivechat.models.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.renekakpo.hivechat.models.User

class UserTypeConverters {
    /**
     * Convert [User] type to a Json string
     */
    @TypeConverter
    fun fromTypeToJson(user: User): String = Gson().toJson(user)

    /**
     * Convert a Json string to [User] type
     */
    @TypeConverter
    fun fromJsonToType(data: String): User = Gson().fromJson(data, User::class.java)
}