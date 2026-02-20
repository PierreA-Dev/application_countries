package com.countries.domain

data class Country(
    val code: String,
    val name: String,
    val emoji: String,
    val capital: String?,
    val currency: String?,
    val continent: String,
    val languages: List<String>
)