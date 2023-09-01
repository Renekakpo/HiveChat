package com.renekakpo.hivechat.data

import android.content.Context
import com.renekakpo.hivechat.database.HiveChatDb

interface HiveChatContainer {
    val userRepository: UserRepository
    val chatRepository: ChatRepository
    val messageRepository: MessageRepository
}

class HiveChatContainerImp(context: Context): HiveChatContainer {
    override val userRepository: UserRepository by lazy {
        UserReposImp(HiveChatDb.getHiveChatDb(context).getUserDao())
    }

    override val chatRepository: ChatRepository by lazy {
        ChatReposImp(HiveChatDb.getHiveChatDb(context).getChatDao())
    }

    override val messageRepository: MessageRepository by lazy {
        MessageReposImp(HiveChatDb.getHiveChatDb(context).getMessageDao())
    }
}