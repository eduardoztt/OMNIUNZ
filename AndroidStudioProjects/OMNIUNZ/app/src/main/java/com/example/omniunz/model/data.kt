package com.example.omniunz.model

import kotlinx.serialization.Serializable

@Serializable
data class MyDataItem(
    val name: String,
    val isDaily: Boolean = true,
)
