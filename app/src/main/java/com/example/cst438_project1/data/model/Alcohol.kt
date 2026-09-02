package com.example.cst438_project1.data.model

import com.google.gson.annotations.SerializedName

data class Alcohol(
    @SerializedName("code")
    val barcode: String?,

    @SerializedName("product_name")
    val name: String?,

    @SerializedName("brands")
    val brand: String?,

    @SerializedName("categories")
    val category: String?,

    @SerializedName("countries")
    val countries: String?,

    @SerializedName("quantity")
    val size: String?,

    @SerializedName("image_url")
    val imageUrl: String?,

    @SerializedName("nutriments")
    val nutriments: Nutriments?
) {
    val abv: Double?
        get() = nutriments?.alcoholByVolume
}

data class Nutriments(
    @SerializedName("alcohol")
    val alcoholByVolume: Double?
)