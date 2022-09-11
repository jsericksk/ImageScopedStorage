package com.kproject.imagescopedstorage.presentation.screens.home.model

import java.util.*

data class WebsiteOption(
    val name: String,
    val url: String
) {
    fun generateRandomUrl(): String = "$url?${UUID.randomUUID()}"
}

internal val websiteOptions = listOf(
    WebsiteOption(
        name = "This Person Does Not Exist",
        url = "https://thispersondoesnotexist.com/image"
    ),
    WebsiteOption(
        name = "This Cat Does Not Exist",
        url = "https://thiscatdoesnotexist.com"
    ),
    WebsiteOption(
        name = "This Horse Does Not Exist",
        url = "https://thishorsedoesnotexist.com"
    )
)