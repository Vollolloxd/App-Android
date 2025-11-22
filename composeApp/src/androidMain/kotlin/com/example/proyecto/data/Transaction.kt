package com.example.proyecto.data

import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    val id: String = System.currentTimeMillis().toString(),
    val description: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val type: String = "", // "income" o "expense"
    val date: Long = System.currentTimeMillis()
)
