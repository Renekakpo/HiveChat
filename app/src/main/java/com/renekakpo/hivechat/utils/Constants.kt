package com.renekakpo.hivechat.utils

object Constants {
    // HiveMQTT broker url
    const val BROKER_URL = "274660101c104209b15b3a200a0ce67e.s2.eu.hivemq.cloud"
    const val TEXT_TYPE = "text"
    const val GROUP_CHAT = "group_chat"
    const val INDIVIDUAL_CHAT = "individual_chat"
    const val APP_PREFERENCE_NAME = "hive_chat_preferences"
    const val TOPIC_BASE_PATH = "hiveChat/broker/client/"

    // Room database config
    const val DB_NAME = "hive_chat_db"
    const val USER_TB_NAME = "users"
    const val CHAT_TB_NAME = "chats"
    const val MESSAGE_TB_NAME = "messages"

    // Message action
    const val NEW_MESSAGE = "new"
    const val INFO_MESSAGE = "info"
    const val EDITED_MESSAGE = "edited"
    const val UPDATE_MESSAGE = "update"
    const val DELETE_MESSAGE = "delete"
    const val DELETE_FOR_ALL_MESSAGE = "delete_for_all"
}