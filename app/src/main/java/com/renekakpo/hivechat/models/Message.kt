package com.renekakpo.hivechat.models

data class Message(
    val id: String,
    val sender: User,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val deliveredTime: Long?,
    val readTime: Long?
)