package ge.zgrdzelidze.messenger.ui.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import ge.zgrdzelidze.messenger.databinding.FragmentUsersBinding
import ge.zgrdzelidze.messenger.ui.adapters.UsersAdapter
import ge.zgrdzelidze.messenger.ui.viewmodels.UsersViewModel
import ge.zgrdzelidze.messenger.ui.viewmodels.ViewState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

    private var _binding: FragmentUsersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UsersViewModel by viewModels()
    private lateinit var usersAdapter: UsersAdapter
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
        observeViewModel()

        binding.ivBackUsers.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        usersAdapter = UsersAdapter(emptyList()) { user ->
            val action = UsersFragmentDirections.actionUsersFragmentToChatFragment(user)
            findNavController().navigate(action)
        }
        binding.rvUsers.apply {
            adapter = usersAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun setupSearch() {
        binding.etSearchUsers.addTextChangedListener { editable ->
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500)
                viewModel.searchUsers(editable.toString())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.users.observe(viewLifecycleOwner) { users ->
            usersAdapter.updateUsers(users)
        }

        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ViewState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.tvInfo.visibility = View.GONE
                }
                is ViewState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInfo.visibility = View.VISIBLE
                    binding.tvInfo.text = state.message
                }
                is ViewState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInfo.visibility = View.VISIBLE
                    binding.tvInfo.text = "დარეგისტრირებული მომხმარებლები არ არიან"
                }
                is ViewState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.tvInfo.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchJob?.cancel()
        _binding = null
    }
}