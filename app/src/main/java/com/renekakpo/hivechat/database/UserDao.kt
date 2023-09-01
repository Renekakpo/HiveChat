package com.renekakpo.hivechat.database

import androidx.room.*
import com.renekakpo.hivechat.models.User
import com.renekakpo.hivechat.utils.Constants.USER_TB_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Query("SELECT * FROM $USER_TB_NAME WHERE uniqueIdentifier = :uniqueIdentifier")
    fun read(uniqueIdentifier: String): User

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)
}