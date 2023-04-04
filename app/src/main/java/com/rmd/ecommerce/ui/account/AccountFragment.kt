package com.rmd.ecommerce.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.rmd.ecommerce.R
import com.rmd.ecommerce.activity.LoginActivity
import com.rmd.ecommerce.databinding.FragmentAccountBinding
import com.rmd.ecommerce.model.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var binding: FragmentAccountBinding
    private val profileRef = FirebaseFirestore.getInstance().collection("profile")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentAccountBinding.bind(view)
        binding.updateProfileBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_account_to_navigation_account_update)
        }

        //check user connection
        FirebaseAuth.getInstance().currentUser?.let { user ->
            getUserInfos(user)
        } ?: run {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun getUserInfos(user: FirebaseUser) = viewLifecycleOwner.lifecycleScope.launch(
        Dispatchers.IO) {
        try {
            val myDocumentSnapshot = profileRef.document(user.uid).get().await()
            val myUser = myDocumentSnapshot.toObject<User>()

            withContext(Dispatchers.Main) {
                myUser?.apply {

                    binding.mailTv.text = email
                    binding.usernameTv.text = name
                    binding.phoneNumberTv.text = phoneNumber
                    Picasso.get().load(imageUrl).into(binding.profileImgv)

                    binding.mailTv.isVisible = email.isNotBlank()
                    binding.usernameTv.isVisible = name.isNotBlank()
                    binding.phoneNumberTv.isVisible = phoneNumber.isNotBlank()
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("getUserInfos : ", "${e.message}")
            }
        }
    }
}
