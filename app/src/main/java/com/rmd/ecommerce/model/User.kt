package com.rmd.ecommerce.model

import com.rmd.ecommerce.model.address.Place
import java.util.Date

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var imageUrl: String = "",
    var phoneNumber: String = "",
    var creationDate: Date = Date(),
    var modificationDate: Date = Date(),
    var rating: Rating,
    var address: Place
)
