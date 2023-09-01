package com.renekakpo.hivechat.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class HiveChatDataStoreRepos(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val TAG = HiveChatDataStoreRepos::class.java.simpleName
        private val SIGN_IN_STATE = booleanPreferencesKey(name = "SIGN_IN_STATE")
        private val HIVE_MQ_CLIENT_ID = stringPreferencesKey(name = "HIVE_MQ_CLIENT_ID")
    }

    suspend fun saveSignInState(hasSignIn: Boolean) {
        dataStore.edit { prefs -> prefs[SIGN_IN_STATE] = hasSignIn }
    }

    val signInState: Flow<Boolean> =  dataStore.data
        .catch {
            if (it is IOException) {
                Log.d(TAG, "Error reading preferences", it)
                emit(value = emptyPreferences())
            } else {
                throw it
            }
        }.map { prefs -> prefs[SIGN_IN_STATE] ?: false }

    suspend fun readClientId(): String {
        val preferences = dataStore.data.first()
        return preferences[HIVE_MQ_CLIENT_ID] ?: ""
    }

    suspend fun saveHiveMQClientId(clientId: String) {
        dataStore.edit { prefs -> prefs[HIVE_MQ_CLIENT_ID] = clientId }
    }
}