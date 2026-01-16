package com.example.hungerkiller.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.hungerkiller.domain.model.Ingredient
import com.example.hungerkiller.domain.model.Recipe

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val imageUrl: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredientsJson: String, // Stored as JSON string
    val youtubeUrl: String?,
    val tagsJson: String, // Stored as JSON string
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun RecipeEntity.toRecipe(): Recipe {
    val ingredients = parseIngredients(ingredientsJson)
    val tags = parseTags(tagsJson)
    return Recipe(
        id = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
        area = area,
        instructions = instructions,
        ingredients = ingredients,
        youtubeUrl = youtubeUrl,
        tags = tags,
        isFavorite = isFavorite
    )
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
        id = id,
        name = name,
        imageUrl = imageUrl,
        category = category,
        area = area,
        instructions = instructions,
        ingredientsJson = ingredientsToJson(ingredients),
        youtubeUrl = youtubeUrl,
        tagsJson = tagsToJson(tags),
        isFavorite = isFavorite
    )
}

// Simple JSON serialization helpers
private fun ingredientsToJson(ingredients: List<Ingredient>): String {
    return ingredients.joinToString("|") { "${it.name}:${it.measure}" }
}

private fun parseIngredients(json: String): List<Ingredient> {
    if (json.isBlank()) return emptyList()
    return json.split("|").mapNotNull { part ->
        val parts = part.split(":")
        if (parts.size == 2) {
            Ingredient(parts[0], parts[1])
        } else null
    }
}

private fun tagsToJson(tags: List<String>): String {
    return tags.joinToString(",")
}

private fun parseTags(json: String): List<String> {
    if (json.isBlank()) return emptyList()
    return json.split(",").filter { it.isNotBlank() }
}
