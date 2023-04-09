package com.rmd.ecommerce.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.rmd.ecommerce.MainActivity
import com.rmd.ecommerce.databinding.ActivitySplashBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var logoUrl: String = ""
    private var duration: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //set full screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val controller =
                    window?.decorView?.javaClass?.getMethod("getWindowInsetsController")
                        ?.invoke(window?.decorView) as WindowInsetsController?
                controller?.hide(WindowInsets.Type.statusBars())
                controller?.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } catch (e: NoSuchMethodException) {
                window?.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
            }
        } else {
            window?.setFlags(FLAG_FULLSCREEN, FLAG_FULLSCREEN)
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the image URL from Firebase Remote Config
        remoteConfig = Firebase.remoteConfig
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        remoteConfig.fetchAndActivate().addOnSuccessListener {
            logoUrl = remoteConfig.getString("splashImage")
            duration = remoteConfig.getLong("splashDuration")

            Glide.with(this).load(logoUrl).into(binding.logo)

            // Start the next activity after a delay
            binding.root.postDelayed({
                startActivity(Intent(this@StartActivity, MainActivity::class.java))
                finish()
            }, duration)
        }
    }
}