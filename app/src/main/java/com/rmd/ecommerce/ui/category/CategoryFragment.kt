package com.rmd.ecommerce.ui.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.*
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.rmd.ecommerce.R
import com.rmd.ecommerce.databinding.FragmentCategoryBinding
import com.rmd.ecommerce.model.Category
import com.rmd.ecommerce.recyclerview.CategoryRecyclerView


class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var categoryRecyclerView: CategoryRecyclerView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //initializations
        binding = FragmentCategoryBinding.bind(view)

        //adapter
        val mLayoutManager = LinearLayoutManager(context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.categoryRecyclerview.layoutManager = mLayoutManager

        // Retrieve the image URL from Firebase Remote Config
        remoteConfig = Firebase.remoteConfig
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)
        getCategories()
    }


    private fun getCategories() {
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