package ge.zgrdzelidze.messenger.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ge.zgrdzelidze.messenger.R
import ge.zgrdzelidze.messenger.data.models.User
import ge.zgrdzelidze.messenger.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUpRegister.setOnClickListener {
            val nickname = binding.etNicknameRegister.text.toString().trim()
            val password = binding.etPasswordRegister.text.toString().trim()
            val profession = binding.etProfession.text.toString().trim()

            val email = "$nickname@messenger.app"

            if (nickname.isNotEmpty() && password.isNotEmpty() && profession.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser!!
                        val user = User(firebaseUser.uid, nickname, profession, "")

                        db.getReference("users").child(firebaseUser.uid).setValue(user)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                                } else {
                                    Toast.makeText(context, "მონაცემთა ბაზის შეცდომა: ${dbTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "რეგისტრაცია ვერ მოხერხდა: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "გთხოვთ შეავსოთ ყველა ველი", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}