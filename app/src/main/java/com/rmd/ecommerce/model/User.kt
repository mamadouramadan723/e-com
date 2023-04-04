package com.rmd.ecommerce.model

import java.util.*

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var imageUrl: String = "",
    var phoneNumber: String = "",
    var score: Int = 0,
    var votersNumber: Int = 0,
    var creationDate: Date = Date(),
    var modificationDate: Date = Date()
)
