package com.renekakpo.hivechat.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.data.ChatRepository
import com.renekakpo.hivechat.data.MessageRepository
import com.renekakpo.hivechat.data.UserRepository
import com.renekakpo.hivechat.helpers.HiveChatClientHelper
import com.renekakpo.hivechat.models.ChatItemUiState
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.StandardCharsets

class ChatListScreenViewModel(
    private val localUserSource: UserRepository,
    localChatsSource: ChatRepository,
    localMessagesSource: MessageRepository,
) : ViewModel() {
    companion object {
        private val TAG = ChatListScreenViewModel::class.java.simpleName
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val chatItemsUiState = localChatsSource.readAllChatStream().map { chats ->
        withContext(Dispatchers.IO) {
            chats.filter { chat ->
                val lastMessage = localMessagesSource.lastMessageDelivered(chat.uniqueIdentifier)
                lastMessage != null
            }.map { chat ->
                val unreadCount = localMessagesSource.unreadCount(chat.uniqueIdentifier, mqClientId)
                val lastMessage = localMessagesSource.lastMessageDelivered(chat.uniqueIdentifier)
                ChatItemUiState(chat = chat, lastMessage = lastMessage, unreadCount = unreadCount)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = listOf()
    )

    private lateinit var currentUser: User

    private val _chatActionState = MutableStateFlow<Message?>(null)
    val chatActionState: Flow<Message?> = _chatActionState.asStateFlow()

    fun initMqClientConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentUser = localUserSource.readUserStream(mqClientId)

                val hiveClient = HiveChatClientHelper.getClient()

                if (hiveClient.state.isConnected) {
                    Log.d(TAG, "Client connected")
                } else {
                    Log.d(TAG, "Client not connected")
                }
            } catch (e: Exception) {
                Log.e(TAG, "${e.message}")
            }
        }
    }

    // TODO: Subscribe to chat's action topics to display typing.. text on UI
    fun subscribeToChatActionTopic(chatId: String) {
        try {
            HiveChatClientHelper.subscribeToTopic(
                topic = "$chatId/action",
                callback = { publish ->
                    try {
                        val payloadJSONString = String(
                            bytes = publish.payloadAsBytes,
                            charset = StandardCharsets.UTF_8
                        )
                        val message = Gson().fromJson(
                            payloadJSONString,
                            Message::class.java
                        )

                        if (message.sender.uniqueIdentifier != mqClientId) {
                            // Update UI state
                            _chatActionState.value = message
                        }
                    } catch (e: Exception) {
                        Log.e("OnReceiveMessage", "${e.message}")
                    }
                },
                whenComplete = { _, throwable ->
                    if (throwable != null) {
                        _chatActionState.value = null
                        Log.e("Chat action subscription", "${throwable.message}")
                    }
                }
            )
        } catch (e: Exception) {
            Log.e("subscribeToChatActionTopic", "${e.message}")
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            chatItemsUiState.value.forEach {
                HiveChatClientHelper.unsubscribeFromTopic(topic = "${it.chat.uniqueIdentifier}/action")
            }
        }
        super.onCleared()
    }
}