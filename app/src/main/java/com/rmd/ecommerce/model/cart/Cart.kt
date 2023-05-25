package com.rmd.ecommerce.model.cart

import java.util.Date

data class Cart(
    var id: Int = 0,
    var userId: String = "",
    var creationDate: Date = Date(),
    var modificationDate: Date = Date(),
    var products: ArrayList<Product> = arrayListOf()
)

data class Product(
    var productId: Int = 0,
    var quantity: Int = 0
)