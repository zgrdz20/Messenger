package ge.zgrdzelidze.messenger.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ge.zgrdzelidze.messenger.databinding.FragmentHomeBinding
import ge.zgrdzelidze.messenger.ui.adapters.ChatAdapter
import ge.zgrdzelidze.messenger.ui.viewmodels.HomeViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(emptyList()) { chat ->
            val action = HomeFragmentDirections.actionHomeFragmentToChatFragment(chat.otherUser)
            findNavController().navigate(action)
        }
        binding.rvChats.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToUsersFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                ge.zgrdzelidze.messenger.R.id.navigation_settings -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToProfileFragment())
                    true
                }

                ge.zgrdzelidze.messenger.R.id.navigation_home -> {
                    true
                }

                else -> false
            }
        }

        binding.etSearch.addTextChangedListener {
            viewModel.filterChats(it.toString())
        }
    }

    private fun observeViewModel() {
        viewModel.chats.observe(viewLifecycleOwner) { chats ->
            chatAdapter.updateChats(chats)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}