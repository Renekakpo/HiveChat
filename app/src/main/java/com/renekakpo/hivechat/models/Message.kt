package com.renekakpo.hivechat.models

data class Message(
    val id: String,
    val chatId: String = "",
    val sender: User,
    val content: String,
    val timestamp: Long,
    val isRead: Boolean,
    val deliveredTime: Long?,
    val readTime: Long?
)

val mockMessages = listOf(
    Message(
        id = "1",
        chatId = "1",
        sender = User("1", "John", "https://example.com/john.png"),
        content = "Hey, how are you doing?",
        timestamp = 1624149360000L,
        isRead = true,
        deliveredTime = 1624149370000L,
        readTime = 1624149380000L
    ),
    Message(
        id = "2",
        chatId = "1",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "I'm doing great! Thanks for asking.",
        timestamp = 1624149420000L,
        isRead = true,
        deliveredTime = 1624149430000L,
        readTime = 1624149440000L
    ),
    Message(
        id = "3",
        chatId = "2",
        sender = User("1", "John", "https://example.com/john.png"),
        content = "What are you up to today?",
        timestamp = 1624149480000L,
        isRead = true,
        deliveredTime = 1624149490000L,
        readTime = 1624149500000L
    ),
    Message(
        id = "4",
        chatId = "2",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Just working on a new project. How about you?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "3",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Just working on a new project. How about you?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "3",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Just working on a new project. How about you?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "4",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Just working on a new project. How about you?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "4",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Just working on a new project. How about you?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "5",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Hi!",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "5",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "What's up?",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "5",
        sender = User("1", "John", "https://example.com/lisa.png"),
        content = "Hi!",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "6",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "Hello!",
        timestamp = 1624149540000L,
        isRead = true,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "6",
        sender = User("2", "Lisa", "https://example.com/lisa.png"),
        content = "How is your day going?",
        timestamp = 1624149540000L,
        isRead = false,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    ),
    Message(
        id = "4",
        chatId = "6",
        sender = User("1", "John", "https://example.com/lisa.png"),
        content = "Hi! It's going well. What about yours?",
        timestamp = 1624149540000L,
        isRead = false,
        deliveredTime = 1624149550000L,
        readTime = 1624149560000L
    )
)