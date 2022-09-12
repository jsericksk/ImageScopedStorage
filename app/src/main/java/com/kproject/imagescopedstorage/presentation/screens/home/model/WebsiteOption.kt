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
        name = title("Person"),
        url = "https://thispersondoesnotexist.com/image"
    ),
    WebsiteOption(
        name = title("Cat"),
        url = "https://thiscatdoesnotexist.com"
    ),
    WebsiteOption(
        name = title("Horse"),
        url = "https://thishorsedoesnotexist.com"
    )
)

private fun title(siteName: String) = "This <b>$siteName</b> Does Not Exist"