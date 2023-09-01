package com.renekakpo.hivechat.data

import com.renekakpo.hivechat.database.UserDao
import com.renekakpo.hivechat.models.User

interface UserRepository {
    suspend fun insertUser(user: User)

    fun readUserStream(uniqueIdentifier: String): User

    suspend fun updateUser(user: User)

    suspend fun deleteUser(user: User)
}

class UserReposImp(private val userDao: UserDao): UserRepository {
    override suspend fun insertUser(user: User) = userDao.insert(user)

    override fun readUserStream(uniqueIdentifier: String): User = userDao.read(uniqueIdentifier)

    override suspend fun updateUser(user: User) = userDao.update(user)

    override suspend fun deleteUser(user: User) = userDao.delete(user)
}