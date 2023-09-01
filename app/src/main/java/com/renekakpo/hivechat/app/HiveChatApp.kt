package com.renekakpo.hivechat.app

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.data.HiveChatContainer
import com.renekakpo.hivechat.data.HiveChatContainerImp
import com.renekakpo.hivechat.datastore.HiveChatDataStoreRepos
import com.renekakpo.hivechat.helpers.HiveChatClientHelper
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.utils.Constants
import com.renekakpo.hivechat.utils.Constants.APP_PREFERENCE_NAME
import com.renekakpo.hivechat.utils.Constants.EDITED_MESSAGE
import com.renekakpo.hivechat.utils.Constants.NEW_MESSAGE
import com.renekakpo.hivechat.utils.Constants.TOPIC_BASE_PATH
import com.renekakpo.hivechat.utils.Constants.UPDATE_MESSAGE
import kotlinx.coroutines.*
import java.nio.charset.StandardCharsets
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCE_NAME)

class HiveChatApp : Application() {

    lateinit var hiveChatContainer: HiveChatContainer

    companion object {
        private val TAG = HiveChatApp::class.java.simpleName

        lateinit var appContext: Context
            private set

        lateinit var hiveChatDataStore: HiveChatDataStoreRepos
            private set

        lateinit var mqClientId: String
            private set

        lateinit var firebaseUsersDbRef: DatabaseReference
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        hiveChatContainer = HiveChatContainerImp(context = this)
        hiveChatDataStore = HiveChatDataStoreRepos(dataStore = dataStore)

        Firebase.initialize(this)
        firebaseUsersDbRef = Firebase.database.reference.child("users")

        CoroutineScope(Dispatchers.IO).launch {
            initClientUuid()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        try {
            HiveChatClientHelper.disconnectClient()
        } catch (e: Exception) {
            Log.e("onTerminate", "${e.message}")
        }
    }

    private suspend fun initClientUuid() {
        val clientId = hiveChatDataStore.readClientId()
        if (clientId.isEmpty()) {
            mqClientId = "${UUID.randomUUID()}"
            hiveChatDataStore.saveHiveMQClientId(
                clientId = mqClientId
            )
        } else {
            mqClientId = clientId
        }

        // Initialize the MQTT client when the app starts
        HiveChatClientHelper.createClient(identifier = clientId)

        // Subscribe to my channel
        HiveChatClientHelper.subscribeToTopic(topic = mqClientId,
            callback = { publish ->
                try {
                    // Convert the payload to the related data class
                    val payloadJSONString =
                        String(publish.payloadAsBytes, StandardCharsets.UTF_8).split("|")
                    val chat = Gson().fromJson(payloadJSONString[0], Chat::class.java)
                    var message = Gson().fromJson(payloadJSONString[1], Message::class.java)

                    // Update the message deliveredTime
                    message = message.copy(deliveredTime = System.currentTimeMillis())

                    // Save the objects to the database
                    CoroutineScope(Dispatchers.IO).launch {
                        hiveChatContainer.chatRepository.insertChat(chat)
                        hiveChatContainer.messageRepository.insertMessage(message)

                        delay(1000)

                        // Update the message action
                        message = message.copy(action = UPDATE_MESSAGE)

                        // Publish message received
                        HiveChatClientHelper.publishOnTopic(
                            topic = "$TOPIC_BASE_PATH${message.chatId}",
                            content = Gson().toJson(message),
                            isRetain = true
                        ) { result, error ->
                            if (result != null) {
                                Log.d("$TAG-subscribeToTopic", "Publish message delivered success")
                            } else {
                                Log.e("$TAG-subscribeToTopic", "${error?.message}")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("OnReceiveMessage", "${e.message}")
                }
            },
            whenComplete = { ack, throwable ->
                if (ack != null) {
                    Log.d(TAG, "Subscribe to topic successfully")
                } else {
                    Log.e(TAG, "${throwable?.message}")
                }
            })

        // Subscribe to all available topics
        hiveChatContainer.chatRepository.readAllChatStream().collect { chats ->
            if (chats.isNotEmpty()) {
                chats.forEach { chat ->
                    HiveChatClientHelper.subscribeToTopic(topic = chat.uniqueIdentifier,
                        callback = { publish ->
                            try {
                                val payloadJSONString = String(
                                    bytes = publish.payloadAsBytes,
                                    charset = StandardCharsets.UTF_8
                                )
                                var message = Gson().fromJson(
                                    payloadJSONString,
                                    Message::class.java
                                )

                                CoroutineScope(Dispatchers.IO).launch {
                                    if (message.sender.uniqueIdentifier != mqClientId) { // I'm not the sender
                                        // It's a new message or the sender edited a previous message
                                        if (message.action == NEW_MESSAGE) {
                                            // Set the deliveredTime and action of the message
                                            message = message.copy(
                                                action = UPDATE_MESSAGE,
                                                deliveredTime = System.currentTimeMillis(),
                                            )

                                            // Save message to local database
                                            hiveChatContainer.messageRepository.insertMessage(
                                                message
                                            )

                                            delay(2000)

                                            // Publish message received
                                            HiveChatClientHelper.publishOnTopic(
                                                topic = "$TOPIC_BASE_PATH${message.chatId}",
                                                content = Gson().toJson(message),
                                                isRetain = true
                                            ) { result, error ->
                                                if (result != null) {
                                                    Log.d(
                                                        "$TAG-subscribeToTopic",
                                                        "Publish message delivered success"
                                                    )
                                                } else {
                                                    Log.e(
                                                        "$TAG-subscribeToTopic",
                                                        "${error?.message}"
                                                    )
                                                }
                                            }
                                        } else { // Update an existing message(edit or delete)
                                            // Save message to local database
                                            hiveChatContainer.messageRepository.updateMessage(
                                                message
                                            )
                                        }
                                    } else { // I'm the sender
                                        if (message.action != NEW_MESSAGE && message.action != EDITED_MESSAGE) {
                                            Log.e("Receive message", "My message was updated")
                                            // Update message delivered time
                                            hiveChatContainer.messageRepository.updateDeliveredTime(
                                                messageId = message.id,
                                                deliveredTime = message.deliveredTime
                                                    ?: System.currentTimeMillis(),
                                                newAction = message.action
                                            )
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("OnReceiveMessage", "${e.message}")
                            }
                        },
                        whenComplete = { ack, throwable ->
                            if (ack != null) {
                                Log.d("$TAG-subscribeToTopic", "Subscribe to topic successfully")
                            } else {
                                Log.e("$TAG-subscribeToTopic", "${throwable?.message}")
                            }
                        })
                }
            }
        }
    }
}