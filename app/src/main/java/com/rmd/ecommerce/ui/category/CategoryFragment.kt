package com.rmd.ecommerce.ui.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.*
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.Gson
import com.rmd.ecommerce.R
import com.rmd.ecommerce.databinding.FragmentCategoryBinding
import com.rmd.ecommerce.model.Category
import com.rmd.ecommerce.model.SubCategory
import com.rmd.ecommerce.recyclerview.CategoryRecyclerView
import com.rmd.ecommerce.recyclerview.SubCategoryRecyclerView
import com.rmd.ecommerce.sharedviewmodel.CategoryViewModel

class CategoryFragment : Fragment(R.layout.fragment_category) {

    private lateinit var binding: FragmentCategoryBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private lateinit var sharedViewModelCategory: CategoryViewModel
    private lateinit var categoryRecyclerView: CategoryRecyclerView
    private lateinit var subCategoryRecyclerView: SubCategoryRecyclerView

    private var selectedCategoryName: String = "Smartphones"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCategoryBinding.bind(view)
        sharedViewModelCategory =
            ViewModelProvider(requireActivity())[CategoryViewModel::class.java]

        val mLayoutManager = LinearLayoutManager(context)
        val gridLayoutManager = GridLayoutManager(context, 3)

        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL

        binding.categoryRecyclerview.layoutManager = mLayoutManager
        binding.subCategoryRecyclerview.layoutManager = gridLayoutManager

        // Retrieve the image URL from Firebase Remote Config
        remoteConfig = Firebase.remoteConfig
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        remoteConfig.setConfigSettingsAsync(configSettings)

        //shared viewModels
        sharedViewModelCategory.selectedCategoryName.observe(viewLifecycleOwner) {
            selectedCategoryName = it
            getSubCategories()
        }
        //functions
        getCategories()

        getSubCategories()
    }

    private fun getSubCategories() {
        val gson = Gson()
        val subCategoryList: ArrayList<SubCategory> = arrayListOf()
        val remote = remoteConfig.fetchAndActivate()

        remote.addOnSuccessListener {
            val stringJson = remoteConfig.getString(selectedCategoryName)
            if (stringJson.isNotEmpty()) {
                val jsonModels = gson.fromJson(stringJson, Array<SubCategory>::class.java)

                val stringBuilder = StringBuilder()
                jsonModels.forEach { subCategory ->
                    stringBuilder.append("$subCategory")
                    Log.d("12345", "$stringBuilder")
                    subCategoryList.add(subCategory)
                }

                subCategoryRecyclerView = SubCategoryRecyclerView(subCategoryList)

                binding.subCategoryRecyclerview.adapter = subCategoryRecyclerView

            } else {
                // probably your remote param not exists
            }
        }
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
                    Log.d("67890", "$stringBuilder")
                    categoryList.add(category)
                }

                categoryRecyclerView = CategoryRecyclerView(
                    requireActivity(), categoryList
                )
                binding.categoryRecyclerview.adapter = categoryRecyclerView

            } else {
                // probably your remote param not exists
            }
        }
    }
}