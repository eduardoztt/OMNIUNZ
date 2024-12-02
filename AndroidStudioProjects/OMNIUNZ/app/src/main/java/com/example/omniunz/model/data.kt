package com.example.omniunz.model

import kotlinx.serialization.Serializable

@Serializable
data class MyDataItem(
    val name: String,
    val data: String,
    val isDaily: Boolean = true,
    val isSemana: Boolean = true,
    val hoje : Boolean = false
)
