package com.renekakpo.hivechat.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renekakpo.hivechat.app.HiveChatApp
import com.renekakpo.hivechat.app.HiveChatApp.Companion.hiveChatDataStore
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.data.UserRepository
import com.renekakpo.hivechat.helpers.HiveChatClientHelper
import com.renekakpo.hivechat.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed interface SignInUiState {
    object Loading : SignInUiState
    data class Error(val message: String) : SignInUiState
    data class Success(val message: String) : SignInUiState
}

class SignInViewModel(private val localUserSource: UserRepository) : ViewModel() {

    var signInUiState: SignInUiState? by mutableStateOf(null)
        private set

    private suspend fun saveUserInput(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                localUserSource.insertUser(user)
                // Save user to firebase database
                HiveChatApp.firebaseUsersDbRef.push().setValue(user).addOnCompleteListener {
                    signInUiState = if (it.isComplete && it.isSuccessful) {
                        SignInUiState.Success("User connected")
                    } else {
                        SignInUiState.Success("User not connected")
                    }
                }
            } catch (e: Exception) {
                Log.e("saveUserInput", "${e.message}")
                signInUiState = SignInUiState.Error("${e.message}")
            }
        }
    }

    suspend fun connectHiveClientToBroker(username: String, password: String) {
        signInUiState = SignInUiState.Loading

        HiveChatClientHelper.connectClient(username, password) { mqtt5ConnAck, throwable ->
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    if (mqtt5ConnAck != null) {
                        // Connection success
                        viewModelScope.launch {
                            hiveChatDataStore.saveSignInState(true)
                            saveUserInput(
                                User(
                                    username = username,
                                    password = password,
                                    uniqueIdentifier = mqClientId
                                )
                            )
                        }
                    } else {
                        // Connection failed
                        hiveChatDataStore.saveSignInState(false)
                        signInUiState = SignInUiState.Error("Client connection failed. Cause: ${throwable?.message}")
                    }
                } catch (e: Exception) {
                    Log.e("connectHiveBroker", "${e.message}")
                    hiveChatDataStore.saveSignInState(false)
                    signInUiState = SignInUiState.Error("${e.message}")
                }
            }
        }
    }
}