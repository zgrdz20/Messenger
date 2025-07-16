package ge.zgrdzelidze.messenger.ui.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.zgrdzelidze.messenger.R
import ge.zgrdzelidze.messenger.data.models.Chat
import ge.zgrdzelidze.messenger.databinding.ItemChatBinding
import ge.zgrdzelidze.messenger.utils.toReadableDate

class ChatAdapter(
    private var chats: List<Chat>,
    private val onChatClicked: (Chat) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount() = chats.size

    fun updateChats(newChats: List<Chat>) {
        chats = newChats
        notifyDataSetChanged()
    }

    inner class ChatViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(chat: Chat) {
            binding.tvChatName.text = chat.otherUser.nickname
            binding.tvLastMessage.text = chat.lastMessage.text
            binding.tvChatTime.text = chat.lastMessage.timestamp.toReadableDate()

            Glide.with(binding.root.context)
                .load(chat.otherUser.profileImageUrl)
                .placeholder(R.drawable.batman_logo)
                .into(binding.ivChatAvatar)
            binding.root.setOnClickListener { onChatClicked(chat) }
        }
    }
}