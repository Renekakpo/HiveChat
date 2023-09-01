package com.renekakpo.hivechat.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.renekakpo.hivechat.models.Chat
import com.renekakpo.hivechat.models.Message
import com.renekakpo.hivechat.models.User
import com.renekakpo.hivechat.models.converters.ListOfUserConverters
import com.renekakpo.hivechat.models.converters.MessageTypeConverters
import com.renekakpo.hivechat.models.converters.UserTypeConverters
import com.renekakpo.hivechat.utils.Constants.DB_NAME

@Database(entities = [User::class, Chat::class, Message::class], version = 1, exportSchema = false)
@TypeConverters(
    ListOfUserConverters::class,
    MessageTypeConverters::class,
    UserTypeConverters::class,
)
abstract class HiveChatDb : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    abstract fun getChatDao(): ChatDao

    abstract fun getMessageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: HiveChatDb? = null

        fun getHiveChatDb(context: Context): HiveChatDb {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context = context,
                    klass = HiveChatDb::class.java,
                    name = DB_NAME
                ).build().also { INSTANCE = it }
            }
        }
    }
}