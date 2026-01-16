package com.example.hungerkiller.domain.model

data class Recipe(
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredients: List<Ingredient>,
    val youtubeUrl: String?,
    val tags: List<String>,
    val isFavorite: Boolean = false
)

data class Ingredient(
    val name: String,
    val measure: String
)