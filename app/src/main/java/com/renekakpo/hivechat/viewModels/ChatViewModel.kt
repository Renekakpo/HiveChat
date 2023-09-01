package com.renekakpo.hivechat.viewModels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.renekakpo.hivechat.app.HiveChatApp
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.data.ChatRepository
import com.renekakpo.hivechat.data.MessageRepository
import com.renekakpo.hivechat.data.UserRepository
import com.renekakpo.hivechat.helpers.HiveChatClientHelper
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.models.User
import com.renekakpo.hivechat.ui.screens.ChatScreen
import com.renekakpo.hivechat.utils.Constants
import com.renekakpo.hivechat.utils.Constants.INDIVIDUAL_CHAT
import com.renekakpo.hivechat.utils.Constants.INFO_MESSAGE
import com.renekakpo.hivechat.utils.Constants.TOPIC_BASE_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets
import java.util.UUID

data class MessagesUiState(val data: List<Message> = listOf())

class ChatViewModel(
    savedStateHandle: SavedStateHandle,
    val localUserSource: UserRepository,
    val localChatsSource: ChatRepository,
    val localMessagesSource: MessageRepository
) : ViewModel() {

    companion object {
        private val TAG = ChatViewModel::class.java.simpleName
        private const val TIMEOUT_MILLIS = 5_000L
    }

    private val jsonChatItem: String = checkNotNull(savedStateHandle[ChatScreen.itemToJsonArg])
    private val chatId: String = checkNotNull(savedStateHandle[ChatScreen.itemUUIDArg])
    val chatItemExists: Boolean = checkNotNull(savedStateHandle[ChatScreen.itemExistsArg])

    private var currentDest: User? = null
    lateinit var currentSender: User

    val messagesUiState: Flow<MessagesUiState> =
        localMessagesSource.readAllMessageStream(chatId = chatId).map { messages ->
            MessagesUiState(messages.sortedBy { it.timestamp })
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = MessagesUiState()
        )

    private val _chatUiState = MutableStateFlow<Chat?>(null)
    val chatUiState: StateFlow<Chat?> = _chatUiState.asStateFlow()

    private val _chatActionState = MutableStateFlow<Message?>(null)
    val chatActionState: Flow<Message?> = _chatActionState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                currentDest = Gson().fromJson(jsonChatItem, User::class.java)
                currentSender = localUserSource.readUserStream(
                    uniqueIdentifier = HiveChatApp.mqClientId
                )
            } catch (e: Exception) {
                Log.e("Initialize current sender and dest.", "${e.message}")
            }
        }
    }

    fun initOrReadChat() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _chatUiState.value = if (chatItemExists) {
                    localChatsSource.readChatStream(chatId)
                } else {
                    Chat(
                        uniqueIdentifier = UUID.randomUUID().toString(),
                        type = INDIVIDUAL_CHAT,
                        name = null,
                        profilePictureUrl = null,
                        participants = listOf(currentDest!!, currentSender),
                        description = null
                    )
                }

                subscribeToChatActionTopic()
            } catch (e: Exception) {
                Log.e("getChatInfo", "${e.message}")
            }
        }
    }

    fun saveMessage(userInput: String, chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save the chat to the database if it does not exist
                if (!chatItemExists) {
                    _chatUiState.value?.let { localChatsSource.insertChat(chat = it) }
                }

                // Create an instance of message
                var message = Message(
                    chatId = chatId,
                    sender = currentSender,
                    content = userInput,
                    isRead = false,
                    sentTime = null,
                    deliveredTime = null,
                    readTime = null
                )

                // Save the message to the local database for offline access
                val generatedId = localMessagesSource.insertMessage(message = message)

                // Update the id of Message
                message = message.copy(id = generatedId)

                // Init the topic
                val topic = if (chatItemExists) {
                    "$TOPIC_BASE_PATH$chatId"
                } else {
                    "$TOPIC_BASE_PATH${currentDest?.uniqueIdentifier}"
                }

                // Send the message to destination
                publishMessage(
                    topic = topic,
                    message = message,
                    chat = if (chatItemExists) null else _chatUiState.value
                )
            } catch (e: Exception) {
                Log.e("saveMessage", "${e.message}")
            }
        }
    }

    private fun publishMessage(topic: String, message: Message, chat: Chat?) {
        try {
            val content = if (chat != null) {
                "${Gson().toJson(chat)}|${Gson().toJson(message)}"
            } else {
                Gson().toJson(message)
            }

            HiveChatClientHelper.publishOnTopic(
                topic = topic,
                content = content,
                isRetain = false
            ) { result, throwable ->
                if (result != null) {
                    viewModelScope.launch(Dispatchers.IO) {
                        try {
                            // Update the sentTime of Message
                            localMessagesSource.updateSentTime(
                                messageId = message.id,
                                sentTime = System.currentTimeMillis()
                            )

                            Log.e("Message sent", "Update message sentTime property")
                        } catch (e: Exception) {
                            Log.e("$TAG-publishMessage-callback", "${throwable?.message}")
                        }
                    }
                } else {
                    Log.e("$TAG-publishMessage-callback", "${throwable?.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("publishMessage", "${e.message}")
        }
    }

    fun subscribeToTopic(topic: String) {
        viewModelScope.launch {
            try {

            } catch (e: Exception) {
                Log.e("subscribeToTopic", "${e.message}")
            }
        }
    }

    private fun subscribeToChatActionTopic() {
        try {
            HiveChatClientHelper.subscribeToTopic(
                topic = "${_chatUiState.value?.uniqueIdentifier}/action",
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

    fun notifyDestOnUserInput(userInput: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val message = Message(
                    action = INFO_MESSAGE,
                    chatId = "${_chatUiState.value?.uniqueIdentifier}",
                    sender = currentSender,
                    content = if (userInput.trim().isNotEmpty() && userInput.trim().isNotBlank()) {
                        "Typing..."
                    } else {
                        ""
                    },
                    sentTime = null,
                    deliveredTime = null,
                    readTime = null,
                    isRead = false
                )

                HiveChatClientHelper.publishOnTopic(
                    topic = "$TOPIC_BASE_PATH${message.chatId}/action",
                    content = Gson().toJson(message),
                    isRetain = false
                ) { _, throwable ->
                    if (throwable != null) {
                        Log.e("notifyDestOnUserInput", "${throwable.message}")
                    }
                }

            } catch (e: Exception) {
                Log.e("notifyDestOnUserInput", "${e.message}")
            }
        }
    }

    override fun onCleared() {
        viewModelScope.launch {
            HiveChatClientHelper.unsubscribeFromTopic(topic = "$chatId/action")
        }
        super.onCleared()
    }
}