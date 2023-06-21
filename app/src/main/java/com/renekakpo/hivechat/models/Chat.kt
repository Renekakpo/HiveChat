package com.renekakpo.hivechat.models

import com.renekakpo.hivechat.utils.Constants.GROUP_CHAT
import com.renekakpo.hivechat.utils.Constants.INDIVIDUAL_CHAT

data class Chat(
    val id: String,
    val type: String,
    val name: String,
    val profilePictureUrl: String?,
    val participants: List<User>,
    val lastMessage: Message,
    val unreadCount: Int
)

// Users
val user1 = User("1", "John Doe", "https://imgtr.ee/images/2023/06/21/m7qZz.jpg")
val user2 = User("2", "Jane Smith", "https://imgtr.ee/images/2023/06/21/m7Mx7.jpg")

val emptyMockChatList = listOf<Chat>()
val mockChatList: List<Chat> = listOf(
    Chat(
        id = "1",
        type = GROUP_CHAT,
        name = "Team Discussion",
        profilePictureUrl = "https://imgtr.ee/images/2023/06/21/ZgX4R.jpg",
        participants = listOf(
            User("u1", "John", null),
            User("u2", "Emily", null),
            User("u3", "Michael", null)
        ),
        lastMessage = Message(
            id = "m1",
            sender = User("u2", "Emily", null),
            content = "Let's discuss the project deadline.",
            timestamp = 1646780400000, // Example timestamp for March 8, 2022, 10:00 AM UTC
            isRead = true,
            deliveredTime = 1646780400000,
            readTime = 1646780415000
        ),
        unreadCount = 0
    ),
    Chat(
        id = "2",
        type = GROUP_CHAT,
        name = "Friends Hangout",
        profilePictureUrl = "https://imgtr.ee/images/2023/06/21/m7npi.jpg",
        participants = listOf(
            User("u4", "Sarah", null),
            User("u5", "David", null),
            User("u6", "Emma", null),
            User("u7", "Mark", null)
        ),
        lastMessage = Message(
            id = "m2",
            sender = User("u5", "David", null),
            content = "Let's meet at the coffee shop tomorrow!",
            timestamp = 1646704000000, // Example timestamp for March 7, 2022, 10:00 AM UTC
            isRead = true,
            deliveredTime = 1646704000000,
            readTime = 1646704050000
        ),
        unreadCount = 2
    ),
    Chat(
        id = "3",
        type = GROUP_CHAT,
        name = "Client Meeting",
        profilePictureUrl = null,
        participants = listOf(
            User("u8", "Alice", null),
            User("u9", "Peter", null)
        ),
        lastMessage = Message(
            id = "m3",
            sender = User("u8", "Alice", null),
            content = "We need to finalize the proposal by Friday.",
            timestamp = 1646638800000, // Example timestamp for March 6, 2022, 10:00 AM UTC
            isRead = false,
            deliveredTime = 1646638800000,
            readTime = null
        ),
        unreadCount = 15
    ),
    Chat(
        id = "4",
        type = GROUP_CHAT,
        name = "Family Group",
        profilePictureUrl = "",
        participants = listOf(
            User("u10", "Alex", null),
            User("u11", "Sophia", null),
            User("u12", "Oliver", null),
            User("u13", "Grace", null)
        ),
        lastMessage = Message(
            id = "m4",
            sender = User("u13", "Grace", null),
            content = "Don't forget to bring gifts for Mom's birthday!",
            timestamp = 1646552400000, // Example timestamp for March 5, 2022, 10:00 AM UTC
            isRead = true,
            deliveredTime = 1646552400000,
            readTime = 1646552435000
        ),
        unreadCount = 0
    ),
    Chat(
        id = "5",
        type = INDIVIDUAL_CHAT,
        name = user1.username,
        profilePictureUrl = user1.profilePictureUrl,
        participants = listOf(user1),
        lastMessage = Message(
            id = "3",
            sender = user1,
            content = "Hey, how are you?",
            timestamp = System.currentTimeMillis() - 120000,
            isRead = true,
            deliveredTime = null,
            readTime = null
        ),
        unreadCount = 115
    ),
    Chat(
        id = "6",
        type = INDIVIDUAL_CHAT,
        name = user2.username,
        profilePictureUrl = user2.profilePictureUrl,
        participants = listOf(user2),
        lastMessage = Message(
            id = "4",
            sender = user2,
            content = "Can you send me the report?",
            timestamp = System.currentTimeMillis() - 180000,
            isRead = true,
            deliveredTime = null,
            readTime = null
        ),
        unreadCount = 0
    )
)
