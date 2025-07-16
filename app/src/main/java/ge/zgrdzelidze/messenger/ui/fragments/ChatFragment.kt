package ge.zgrdzelidze.messenger.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import ge.zgrdzelidze.messenger.R
import ge.zgrdzelidze.messenger.databinding.FragmentChatBinding
import ge.zgrdzelidze.messenger.ui.adapters.MessageAdapter
import ge.zgrdzelidze.messenger.ui.viewmodels.ChatViewModel

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ChatViewModel by viewModels()
    private val args: ChatFragmentArgs by navArgs()
    private lateinit var messageAdapter: MessageAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        viewModel.initChat(args.otherUser.uid)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupSendButton()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvUserName.text = args.otherUser.nickname
        binding.tvUserProfession.text = args.otherUser.profession

        Glide.with(this)
            .load(args.otherUser.profileImageUrl)
            .placeholder(R.drawable.batman_logo)
            .into(binding.ivUserAvatar)
    }

    private fun setupRecyclerView() {
        messageAdapter = MessageAdapter(emptyList())
        binding.rvMessages.apply {
            adapter = messageAdapter
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupSendButton() {
        binding.btnSend.setOnClickListener {
            val messageText = binding.etMessage.text.toString()
            viewModel.sendMessage(messageText, args.otherUser.uid)
            binding.etMessage.text.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.messages.observe(viewLifecycleOwner) { messages ->
            messageAdapter.updateMessages(messages)
            binding.rvMessages.scrollToPosition(messages.size - 1)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}