package ge.zgrdzelidze.messenger.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import ge.zgrdzelidze.messenger.data.models.Chat
import ge.zgrdzelidze.messenger.data.models.Message
import ge.zgrdzelidze.messenger.data.models.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class HomeViewModel : ViewModel() {
    private val db = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats

    private var allChats = listOf<Chat>()

    init {
        loadChats()
    }

    private fun loadChats() {
        if (currentUserId == null) return

        val userChatsRef = db.getReference("user-chats").child(currentUserId)

        viewModelScope.launch {
            val chatIdsSnapshot = userChatsRef.get().await()
            val chatIds = chatIdsSnapshot.children.mapNotNull { it.key }

            val loadedChats = mutableListOf<Chat>()

            for (chatId in chatIds) {
                val lastMessageSnapshot =
                    db.getReference("chats").child(chatId).child("lastMessage").get().await()
                val lastMessage = lastMessageSnapshot.getValue(Message::class.java)

                val otherUserId = chatId.replace(currentUserId, "").replace("_", "")

                val otherUserSnapshot = db.getReference("users").child(otherUserId).get().await()
                val otherUser = otherUserSnapshot.getValue(User::class.java)

                if (lastMessage != null && otherUser != null) {
                    loadedChats.add(Chat(otherUser, lastMessage))
                }
            }

            allChats = loadedChats.sortedByDescending { it.lastMessage.timestamp }
            _chats.value = allChats
        }
    }

    fun filterChats(query: String) {
        if (query.isEmpty()) {
            _chats.value = allChats
        } else {
            _chats.value = allChats.filter {
                it.otherUser.nickname.contains(query, ignoreCase = true)
            }
        }
    }
}