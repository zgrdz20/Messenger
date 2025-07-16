package ge.zgrdzelidze.messenger.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ge.zgrdzelidze.messenger.R
import ge.zgrdzelidze.messenger.data.models.User
import ge.zgrdzelidze.messenger.databinding.ItemUserBinding

class UsersAdapter(
    private var users: List<User>,
    private val onUserClicked: (User) -> Unit
) : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount() = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.tvUserName.text = user.nickname
            binding.tvUserProfession.text = user.profession
            Glide.with(binding.root.context)
                .load(user.profileImageUrl)
                .placeholder(R.drawable.batman_logo)
                .into(binding.ivUserAvatar)
            binding.root.setOnClickListener { onUserClicked(user) }
        }
    }
}