package com.renekakpo.hivechat.helpers

import android.util.Log
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.connect.connack.Mqtt5ConnAck
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PublishResult
import com.hivemq.client.mqtt.mqtt5.message.subscribe.suback.Mqtt5SubAck
import com.hivemq.client.mqtt.mqtt5.message.unsubscribe.Mqtt5Unsubscribe
import com.renekakpo.hivechat.utils.Constants.BROKER_URL
import com.renekakpo.hivechat.utils.Constants.TOPIC_BASE_PATH
import java.nio.charset.StandardCharsets

object HiveChatClientHelper {
    private lateinit var hiveMQClient: Mqtt5Client

    fun createClient(identifier: String) {
        // Ensure the call to this function won't reset the instance of hiveMQClient
        if (!::hiveMQClient.isInitialized) {
            try {
                hiveMQClient = Mqtt5Client.builder()
                    .identifier(identifier) // use a unique identifier
                    .serverHost(BROKER_URL)
                    .automaticReconnectWithDefaultConfig() // the client automatically reconnects
                    .serverPort(8883) // this is the port of your cluster, for mqtt it is the default port 8883
                    .sslWithDefaultConfig() // establish a secured connection to HiveMQ Cloud using TLS
                    .build()
            } catch (e: Exception) {
                Log.e("createClient", "${e.message}")
            }
        }
    }

    fun connectClient(
        username: String,
        password: String,
        whenComplete: (mqtt5ConnAck: Mqtt5ConnAck?, throwable: Throwable?) -> Unit
    ) {
        try {
            hiveMQClient.toAsync().connectWith()
                .simpleAuth() // using authentication, which is required for a secure connection
                .username(username) // use the username and password you just created
                .password(password.toByteArray(charset = StandardCharsets.UTF_8))
                .applySimpleAuth()
                .cleanStart(false)
                .willPublish() // the last message, before the client disconnects
                .topic(TOPIC_BASE_PATH)
                .qos(MqttQos.AT_LEAST_ONCE) // The message will be deliver at least once
                .retain(true) // Store the message on the broker so new subscribers can receive even if they were not connect
                .noMessageExpiry()
                .payload("DISCONNECTED".toByteArray(charset = StandardCharsets.UTF_8))
                .applyWillPublish()
                .noKeepAlive()
                .send()
                .whenComplete(whenComplete)
        } catch (e: Exception) {
            Log.e("connectClient", "${e.message}")
        }
    }

    fun publishOnTopic(
        topic: String,
        content: String,
        isRetain: Boolean,
        whenComplete: (Mqtt5PublishResult?, Throwable?) -> Unit
    ) {
        try {
            hiveMQClient.toAsync().publishWith()
                .topic(topic)
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .payload(content.toByteArray(charset = StandardCharsets.UTF_8))
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(isRetain)
                .noMessageExpiry()
                .send()
                .whenComplete(whenComplete)
        } catch (e: Exception) {
            Log.e("publishOnTopic", "${e.message}")
        }
    }

    fun subscribeToTopic(
        topic: String,
        callback: (Mqtt5Publish) -> Unit,
        whenComplete: (Mqtt5SubAck?, Throwable?) -> Unit
    ) {
        try {
            hiveMQClient.toAsync().subscribeWith()
                .topicFilter("$TOPIC_BASE_PATH$topic")
                .callback { publish ->
                    Log.d("subscribeToTopic - topic", "${publish.topic}")
                    Log.d(
                        "subscribeToTopic - payload",
                        String(publish.payloadAsBytes, StandardCharsets.UTF_8)
                    )
                    callback(publish)
                }
                .send()
                .whenComplete { mqtt5SubAck, throwable ->
                    whenComplete(mqtt5SubAck, throwable)
                }
        } catch (e: Exception) {
            Log.e("subscribeToTopic", "${e.message}")
        }
    }

    fun unsubscribeFromTopic(topic: String) {
        try {
            val unsubscribe = Mqtt5Unsubscribe.builder()
                .topicFilter("$TOPIC_BASE_PATH$topic")
                .build()

            hiveMQClient.toBlocking().unsubscribe(unsubscribe)
        } catch (e: Exception) {
            Log.e("", "${e.message}")
        }
    }

    fun disconnectClient() {
        try {
            hiveMQClient.toAsync().disconnectWith().send()
        } catch (e: Exception) {
            Log.e("disconnectClient", "${e.message}")
        }
    }

    fun getClient(): Mqtt5Client {
        return hiveMQClient
    }
}