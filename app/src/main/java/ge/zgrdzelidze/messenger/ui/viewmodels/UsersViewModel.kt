package ge.zgrdzelidze.messenger.ui.viewmodels


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ge.zgrdzelidze.messenger.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UsersViewModel : ViewModel() {

    private val db = FirebaseDatabase.getInstance().getReference("users")
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private var allUsers = listOf<User>()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        _viewState.value = ViewState.Loading
        viewModelScope.launch {
            try {
                val snapshot = db.get().await()
                val userList = snapshot.children.mapNotNull { it.getValue(User::class.java) }

                allUsers = userList.filter { it.uid != currentUserId }
                _users.value = allUsers
                _viewState.value = if (allUsers.isEmpty()) ViewState.Empty else ViewState.Success
            } catch (e: Exception) {
                _viewState.value = ViewState.Error("მომხმარებლების ჩატვირთვა ვერ მოხერხდა")
            }
        }
    }

    fun searchUsers(query: String) {
        if (query.length < 3) {
            _users.value = allUsers
            return
        }
        val filteredList = allUsers.filter {
            it.nickname.contains(query, ignoreCase = true) ||
                    it.profession.contains(query, ignoreCase = true)
        }
        _users.value = filteredList

        if (filteredList.isEmpty()) {
            _viewState.value = ViewState.Error("მომხმარებელი ვერ მოიძებნა")
        } else {
            _viewState.value = ViewState.Success
        }
    }
}

sealed class ViewState {
    object Loading : ViewState()
    object Success : ViewState()
    object Empty : ViewState()
    data class Error(val message: String) : ViewState()
}