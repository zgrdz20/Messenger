package ge.zgrdzelidze.messenger.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import ge.zgrdzelidze.messenger.R
import ge.zgrdzelidze.messenger.databinding.FragmentProfileBinding
import ge.zgrdzelidze.messenger.ui.viewmodels.ProfileViewModel

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            binding.ivProfilePicture.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.ivProfilePicture.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {
            val nickname = binding.etNicknameProfile.text.toString().trim()
            val profession = binding.etProfessionProfile.text.toString().trim()

            if (nickname.isNotEmpty() && profession.isNotEmpty()) {
                viewModel.updateProfile(nickname, profession, selectedImageUri)
            } else {
                Toast.makeText(context, "შეავსეთ ყველა ველი", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSignOut.setOnClickListener {
            viewModel.signOut()
            if (FirebaseAuth.getInstance().currentUser == null) {
                val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }



        binding.fab.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToUsersFragment())
        }

        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToHomeFragment())
                    true
                }
                R.id.navigation_settings -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun observeViewModel() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.etNicknameProfile.setText(it.nickname)
                binding.etProfessionProfile.setText(it.profession)
                Glide.with(this)
                    .load(it.profileImageUrl)
                    .placeholder(R.drawable.batman_logo)
                    .error(R.drawable.batman_logo)
                    .into(binding.ivProfilePicture)
            }
        }

        viewModel.updateStatus.observe(viewLifecycleOwner) { isSuccess ->
            if (isSuccess) {
                Toast.makeText(context, "პროფილი წარმატებით განახლდა", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}