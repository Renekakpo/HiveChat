package com.renekakpo.hivechat.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.data.UserRepository
import com.renekakpo.hivechat.helpers.HiveChatClientHelper
import com.renekakpo.hivechat.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashScreenViewModel(localUserSource: UserRepository) : ViewModel() {
    var user = MutableStateFlow<User?>(null)
        private set

    private val _clientConnected = MutableStateFlow(false)
    val clientConnected: StateFlow<Boolean> = _clientConnected.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            user.value = localUserSource.readUserStream(mqClientId)
        }
    }

    fun connectHiveMqttClient(username: String, password: String) {
        viewModelScope.launch {
            if (HiveChatClientHelper.getClient().state.isConnected) {
                _clientConnected.value = true
            } else {
                HiveChatClientHelper.connectClient(username, password) { mqtt5ConnAck, throwable ->
                    if (throwable != null) {
                        Log.e("initHiveMQClient", "${throwable.message}")
                    } else if (mqtt5ConnAck != null) {
                        Log.d(
                            "initHiveMQClient - success",
                            "${HiveChatClientHelper.getClient().state.isConnected}"
                        )
                        Log.d("initHiveMQClient - success", "${mqtt5ConnAck.isSessionPresent}")
                        Log.d("initHiveMQClient - success", "${mqtt5ConnAck.reasonString}")
                        Log.d("initHiveMQClient - success", "${mqtt5ConnAck.responseInformation}")
                        Log.d(
                            "initHiveMQClient - success",
                            "${mqtt5ConnAck.reasonCode.code} / ${mqtt5ConnAck.reasonCode.name}"
                        )
                        _clientConnected.value = true
                    }
                }
            }
        }
    }
}