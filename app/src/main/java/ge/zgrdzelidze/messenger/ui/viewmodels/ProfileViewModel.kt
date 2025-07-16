package ge.zgrdzelidze.messenger.ui.viewmodels


import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.zgrdzelidze.messenger.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProfileViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().getReference("users")
    private val storage = FirebaseStorage.getInstance().reference

    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _updateStatus = MutableLiveData<Boolean>()
    val updateStatus: LiveData<Boolean> = _updateStatus

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            val userSnapshot = db.child(uid).get().await()
            _user.value = userSnapshot.getValue(User::class.java)
        }
    }

    fun updateProfile(nickname: String, profession: String, imageUri: Uri?) {
        viewModelScope.launch {
            val uid = auth.currentUser?.uid ?: return@launch
            var imageUrl = _user.value?.profileImageUrl ?: ""

            if (imageUri != null) {
                imageUrl = storage.child("profile_images/$uid").putFile(imageUri).await()
                    .storage.downloadUrl.await().toString()
            }

            val updatedUser = User(uid, nickname, profession, imageUrl)
            db.child(uid).setValue(updatedUser).await()
            _updateStatus.value = true
        }
    }

    fun signOut() {
        auth.signOut()
    }
}