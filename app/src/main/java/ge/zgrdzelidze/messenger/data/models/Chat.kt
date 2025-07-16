package ge.zgrdzelidze.messenger.data.models

data class Chat(
    val otherUser: User = User(),
    val lastMessage: Message = Message()
)