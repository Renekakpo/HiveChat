package com.renekakpo.hivechat.utils

import java.text.SimpleDateFormat
import java.util.*

fun getTimeFromMilliseconds(timeInMillis: Long?): String {
    return if (timeInMillis == null) {
        ""
    } else {
        val formatter = SimpleDateFormat("HH:mm", Locale.ROOT)
        formatter.timeZone = TimeZone.getDefault()
        val date = Date(timeInMillis)
        formatter.format(date)
    }
}