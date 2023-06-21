package com.renekakpo.hivechat.models

data class User(
    val id: String,
    val username: String,
    val profilePictureUrl: String? = null
)