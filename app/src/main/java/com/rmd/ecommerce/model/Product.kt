package com.rmd.ecommerce.model

import com.rmd.ecommerce.model.address.Place

data class Product(
    var id: Int = 0,
    var name: String = "",
    var description: String = "",
    var marques: ArrayList<String> = arrayListOf(),
    var imageUrl: ArrayList<String> = arrayListOf(),
    var subCategoryId: ArrayList<Int> = arrayListOf(),
    var quantity: Int = 0,
    var price: Double = 0.0,
    var promotion: Double  = 0.0,
    var rating: Rating,
    var address: Place
)