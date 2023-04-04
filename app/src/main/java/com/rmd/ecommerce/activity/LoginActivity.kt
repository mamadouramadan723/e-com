package com.rmd.ecommerce.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.FacebookSdk
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rmd.ecommerce.MainActivity
import com.rmd.ecommerce.R
import com.rmd.ecommerce.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*


class LoginActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var providers: List<AuthUI.IdpConfig>
    private lateinit var listener: FirebaseAuth.AuthStateListener

    private val profileRef = FirebaseFirestore.getInstance().collection("profile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()
        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(applicationContext)

        checkUserConnection()
    }

    private fun checkUserConnection() {
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            loginOrRegister()
        }
    }

    private fun loginOrRegister() {
        val actionCodeSettings = ActionCodeSettings
            .newBuilder()
            .setHandleCodeInApp(true)
            .setUrl("https://rmdecom.page.link/home")
            .setAndroidPackageName(packageName, true, null)
            .build()

        providers = arrayListOf(
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build(),
            AuthUI.IdpConfig.GitHubBuilder().build(),
            AuthUI.IdpConfig.FacebookBuilder().build(),
            AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn()
                .setActionCodeSettings(actionCodeSettings).build()
        )
        listener = FirebaseAuth.AuthStateListener { auth ->
            val user = auth.currentUser
            if (user != null) {
                //The user is already connected
                Toast.makeText(
                    this@LoginActivity,
                    "${user.displayName} Connected",
                    Toast.LENGTH_LONG
                ).show()
            } else {

                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, true)
                        .build(), AUTH_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTH_REQUEST_CODE && resultCode == RESULT_OK) {
            val response = IdpResponse.fromResultIntent(data)

            if (response != null && response.isNewUser) {
                val user = firebaseAuth.currentUser
                user?.let {
                    val myUserId = user.uid
                    val myUserMail = user.email ?: ""
                    val myUsername = user.displayName ?: ""
                    val myPhoneNumber = user.phoneNumber ?: ""
                    val myUserImageUrl =
                        "https://firebasestorage.googleapis.com/v0/b/real-state-723.appspot.com/o/default%2Fworkspace.png?alt=media&token=d2624d4b-8656-403b-9d6e-9bbfebaebb25"

                    val myUser = User(
                        myUserId,
                        myUsername,
                        myUserMail,
                        myUserImageUrl,
                        myPhoneNumber,
                        0,
                        0,
                        Date(),
                        Date()

                    )
                    uploadData(myUser)
                }
            } else {
                Toast.makeText(this, "Welcome back", Toast.LENGTH_LONG).show()
                //As soon after authentication, return to main
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun uploadData(myUser: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            profileRef.document(myUser.id).set(myUser).await()

            //As we can't directly access to UI within a coroutine, we use withContext
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@LoginActivity,
                    "User added successfully",
                    Toast.LENGTH_LONG
                ).show()

                //As soon as I register, return to main
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {

            //As we can't directly access to UI within a coroutine, we use withContext
            withContext(Dispatchers.Main) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(listener)
    }

    override fun onStop() {
        firebaseAuth.removeAuthStateListener(listener)
        super.onStop()
    }

    companion object {
        private const val AUTH_REQUEST_CODE = 1
    }
}