package com.renekakpo.hivechat.utils

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.firebase.database.DataSnapshot
import com.renekakpo.hivechat.app.HiveChatApp
import com.renekakpo.hivechat.models.User
import kotlinx.coroutines.CoroutineScope

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [HiveChatApp].
 */
fun CreationExtras.hiveChatApp(): HiveChatApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HiveChatApp)