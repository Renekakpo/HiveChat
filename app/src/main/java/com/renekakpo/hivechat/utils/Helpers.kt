package com.renekakpo.hivechat.utils

import android.content.Context
import android.widget.Toast
import com.renekakpo.hivechat.models.User
import java.text.SimpleDateFormat
import java.util.*

fun getTimeFromMilliseconds(timeInMillis: Long?): String {
    return if (timeInMillis == null) {
        ""
    } else {
        val formatter = SimpleDateFormat("HH:mm", Locale.ROOT)
        formatter.timeZone = TimeZone.getDefault()
        formatter.format(timeInMillis)
    }
}

fun showMessage(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun createUserFromHashMap(data: HashMap<String, Any>): User {
    val username = data["username"] as? String ?: ""
    val password = data["password"] as? String ?: ""
    val uniqueIdentifier = data["uniqueIdentifier"] as? String ?: ""
    val profilePictureUrl = data["profilePictureUrl"] as? String
    val info = data["info"] as? String
    val createdAt = data["createdAt"] as? Long ?: System.currentTimeMillis()
    val updatedAt = data["updatedAt"] as? Long ?: System.currentTimeMillis()

    return User(username, password, uniqueIdentifier, profilePictureUrl, info, createdAt, updatedAt)
}