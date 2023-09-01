package com.renekakpo.hivechat.models

data class ChatItemUiState(
    val chat: Chat,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0
)
