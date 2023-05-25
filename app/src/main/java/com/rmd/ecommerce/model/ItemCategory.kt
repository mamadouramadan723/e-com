package com.rmd.ecommerce.model

data class ItemCategory(
    var id: Int = 0,
    var name: String = "",
    var imageUrl: String = "",
    var description: String = "",
    var categoryId: ArrayList<Int> = arrayListOf(),
)