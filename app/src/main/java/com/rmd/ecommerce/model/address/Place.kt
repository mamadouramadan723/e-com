package com.rmd.ecommerce.model.address

data class Place(
    var id: String = "",
    var name: String = "",
    var address: String = "",
    var latLng: LatLong? = LatLong(0.0, 0.0),
)