package ge.zgrdzelidze.messenger.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ge.zgrdzelidze.messenger.data.models.Message
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private lateinit var messagesRef: DatabaseReference
    private lateinit var chatListener: ValueEventListener

    fun initChat(otherUserId: String) {
        val chatId = getChatId(otherUserId)
        messagesRef = db.getReference("messages").child(chatId)

        chatListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                _messages.value = messageList
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        messagesRef.addValueEventListener(chatListener)
    }

    fun sendMessage(text: String, otherUserId: String) {
        if (text.isBlank()) return

        val chatId = getChatId(otherUserId)
        val message = Message(
            senderId = currentUserId,
            text = text,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            messagesRef.push().setValue(message)

            db.getReference("chats").child(chatId).child("lastMessage").setValue(message)

            db.getReference("user-chats").child(currentUserId).child(chatId).setValue(true)
            db.getReference("user-chats").child(otherUserId).child(chatId).setValue(true)
        }
    }

    private fun getChatId(otherUserId: String): String {
        return if (currentUserId < otherUserId) {
            "${currentUserId}_${otherUserId}"
        } else {
            "${otherUserId}_${currentUserId}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::messagesRef.isInitialized) {
            messagesRef.removeEventListener(chatListener)
        }
    }
}