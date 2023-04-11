package com.rmd.ecommerce.sharedviewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CategoryViewModel : ViewModel() {
    var selectedCategoryName = MutableLiveData<String>()

    fun setSelectedCategoryName(name: String) {
        selectedCategoryName.value = name
    }
}