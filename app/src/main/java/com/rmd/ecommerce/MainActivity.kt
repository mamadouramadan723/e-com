package com.rmd.ecommerce

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.FacebookSdk
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rmd.ecommerce.activity.LoginActivity
import com.rmd.ecommerce.activity.SettingActivity
import com.rmd.ecommerce.databinding.ActivityMainBinding
import com.rmd.ecommerce.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    private val profileRef = FirebaseFirestore.getInstance().collection("profile")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_category,
                R.id.navigation_cart,
                R.id.navigation_wishlist,
                R.id.navigation_account
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //to Handle email link verified to be authenticated
        if (AuthUI.canHandleIntent(intent)) {
            val link = intent.data.toString()
            val providers: List<AuthUI.IdpConfig> = listOf(
                AuthUI.IdpConfig.EmailBuilder().build()
            )

            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                    .setEmailLink(link)
                    .setAvailableProviders(providers)
                    .build(), AUTH_REQUEST_CODE
            )
            uploadUserInfos()
        }
    }

    private fun uploadUserInfos() {
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        user?.let {
            val myUserId = user.uid
            val myUserMail = user.email ?: ""
            val myUsername = user.displayName ?: ""
            val myPhoneNumber = user.phoneNumber ?: ""
            val creationDate = Date()
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
                creationDate,
                creationDate
            )
            uploadData(myUser)
        }
    }

    private fun uploadData(myUser: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            profileRef.document(myUser.id).set(myUser).await()

            //As we can't directly access to UI within a coroutine, we use withContext
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity,
                    "User added successfully",
                    Toast.LENGTH_LONG
                ).show()

                //As soon as I register, return to main
                val intent = Intent(this@MainActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {

            //As we can't directly access to UI within a coroutine, we use withContext
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        super.onSupportNavigateUp()
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        val item = menu.findItem(R.id.action_signout)
        if (FirebaseAuth.getInstance().currentUser == null) {
            item.title = "Sign In"
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id: Int = item.itemId
        if (id == R.id.action_signout) {
            if (FirebaseAuth.getInstance().currentUser != null) {
                Toast.makeText(this, "Disconnection", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
                refresh()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else if (id == R.id.action_settings) {
            startActivity(Intent(this, SettingActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        finish()
        startActivity(intent)
    }

    companion object {
        private const val AUTH_REQUEST_CODE = 1
    }
}