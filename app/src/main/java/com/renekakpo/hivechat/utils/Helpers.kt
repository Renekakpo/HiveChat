package com.renekakpo.hivechat.utils

import java.text.SimpleDateFormat
import java.util.*

fun getTimeFromMilliseconds(timeInMillis: Long): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.ROOT)
    formatter.timeZone = TimeZone.getDefault()
    val date = Date(timeInMillis)
    return formatter.format(date)
}