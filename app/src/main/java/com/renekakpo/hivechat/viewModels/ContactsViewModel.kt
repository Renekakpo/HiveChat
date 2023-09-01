package com.renekakpo.hivechat.viewModels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.renekakpo.hivechat.app.HiveChatApp
import com.renekakpo.hivechat.app.HiveChatApp.Companion.mqClientId
import com.renekakpo.hivechat.models.User
import com.renekakpo.hivechat.utils.createUserFromHashMap

sealed interface ContactsUiState {
    object Loading : ContactsUiState
    data class Error(val message: String) : ContactsUiState
    data class Success(val data: List<User>) : ContactsUiState
}

class ContactsViewModel : ViewModel() {
    var contactsUiState: ContactsUiState by mutableStateOf(ContactsUiState.Loading)
        private set

    init {

        contactsUiState = ContactsUiState.Loading

        // Retrieve the list of data from the dataSnapshot
        val userList = mutableListOf<User>()

        HiveChatApp.firebaseUsersDbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    var currentUser: User? = null
                    val dataChildren = dataSnapshot.children

                    for (dataChild in dataChildren) {
                        val hashData: HashMap<String, Any> = dataChild.value as HashMap<String, Any>
                        val user = createUserFromHashMap(hashData)
                        userList.add(user)
                        if (user.uniqueIdentifier == mqClientId) {
                            currentUser = user
                        }
                    }

                    val sortedList = mutableListOf<User>()
                    // Sort the list and put current user at the top of the list
                    sortedList.addAll(userList.filter { it.uniqueIdentifier != mqClientId }
                        .sortedBy { it.username })
                    currentUser?.let { sortedList.add(0, it) }

                    contactsUiState = ContactsUiState.Success(sortedList)
                } catch (e: Exception) {
                    Log.e("firebaseUsersDbRef", "${e.message}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                contactsUiState = ContactsUiState.Error(error.details)
            }
        })
    }
}