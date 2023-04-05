package com.rmd.ecommerce.ui.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.google.gson.Gson
import com.rmd.ecommerce.R
import com.rmd.ecommerce.databinding.FragmentCategoryBinding
import com.rmd.ecommerce.model.Category
import com.rmd.ecommerce.recyclerview.CategoryRecyclerView


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig
    private lateinit var categoryRecyclerView: CategoryRecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializations
        binding = FragmentCategoryBinding.bind(view)

        //adapter
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoryRecyclerview.layoutManager = mLayoutManager

        // [START get_remote_config_instance]
        remoteConfig = Firebase.remoteConfig
        // [END get_remote_config_instance]

        // [START enable_dev_mode]
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 60
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        // [END enable_dev_mode]

        // [START set_default_values]
        //remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        // [END set_default_values]

        // [START add_config_update_listener]
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                Log.d(TAG, "Updated keys: " + configUpdate.updatedKeys);

                if (configUpdate.updatedKeys.contains("category")) {
                    remoteConfig.activate().addOnCompleteListener {
                        categoryUpdate()
                    }
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: " + error.code, error)
            }
        })
        // [END add_config_update_listener]
        categoryUpdate()
    }

    // [START display_welcome_message]
    private fun categoryUpdate() {
        val gson = Gson()
        val categoryList: ArrayList<Category> = arrayListOf()
        val remote = remoteConfig.fetchAndActivate()
        remote.addOnSuccessListener {
            val stringJson = remoteConfig.getString("category")
            if (stringJson.isNotEmpty()) {
                val jsonModels = gson.fromJson(stringJson, Array<Category>::class.java)

                val stringBuilder = StringBuilder()
                jsonModels.forEach { category ->
                    stringBuilder.append("$category")
                    categoryList.add(category)
                }
                Log.d("1234 :+++", "$stringBuilder")
                categoryRecyclerView = CategoryRecyclerView(categoryList)
                binding.categoryRecyclerview.adapter = categoryRecyclerView

            } else {
                // probably your remote param not exists
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}