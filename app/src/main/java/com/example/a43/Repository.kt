package com.example.a43

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Repository(
    val id: Int,
    @SerialName(value = "full_name")
    val name: String,
    val description: String,
    @SerialName(value = "stargazers_count")
    val stargazersCount: Int,
    val language: String?
)

