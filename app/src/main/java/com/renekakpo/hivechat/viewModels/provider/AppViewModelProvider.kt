package com.renekakpo.hivechat.viewModels.provider

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.renekakpo.hivechat.utils.hiveChatApp
import com.renekakpo.hivechat.viewModels.*

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val userRepository = hiveChatApp().hiveChatContainer.userRepository

            SplashScreenViewModel(localUserSource = userRepository)
        }

        initializer {
            val userRepository = hiveChatApp().hiveChatContainer.userRepository

            SignInViewModel(localUserSource = userRepository)
        }

        initializer {
            val userRepository = hiveChatApp().hiveChatContainer.userRepository
            val chatsRepository = hiveChatApp().hiveChatContainer.chatRepository
            val messagesRepository = hiveChatApp().hiveChatContainer.messageRepository

            ChatListScreenViewModel(
                localUserSource = userRepository,
                localMessagesSource = messagesRepository,
                localChatsSource = chatsRepository
            )
        }

        initializer {
            ContactsViewModel()
        }

        initializer {
            val userRepository = hiveChatApp().hiveChatContainer.userRepository
            val messagesRepository = hiveChatApp().hiveChatContainer.messageRepository
            val chatsRepository = hiveChatApp().hiveChatContainer.chatRepository

            ChatViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                localUserSource = userRepository,
                localChatsSource = chatsRepository,
                localMessagesSource = messagesRepository
            )
        }
    }
}