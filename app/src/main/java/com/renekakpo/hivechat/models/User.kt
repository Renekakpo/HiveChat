package com.renekakpo.hivechat.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.firebase.database.IgnoreExtraProperties
import com.renekakpo.hivechat.utils.Constants.USER_TB_NAME

@Entity(
    tableName = USER_TB_NAME,
    indices = [Index(value = ["uniqueIdentifier"], unique = true)]
)
@IgnoreExtraProperties
data class User(
    var username: String,
    var password: String = "",
    @PrimaryKey
    var uniqueIdentifier: String,
    var profilePictureUrl: String? = null,
    var info: String? = null,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
)