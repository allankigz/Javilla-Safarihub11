package com.kigz.javillasafarihub.safety

data class SafetyItem(
    val title: String,
    val description: String,
    val actionLabel: String? = null,
    val phoneNumber: String? = null
)