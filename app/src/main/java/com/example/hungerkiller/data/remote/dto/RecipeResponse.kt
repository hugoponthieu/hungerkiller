package com.example.hungerkiller.data.remote.dto

import com.example.hungerkiller.domain.model.Ingredient
import com.example.hungerkiller.domain.model.Recipe
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlin.String

@JsonClass(generateAdapter = true)
data class RecipeListResponse(
    @Json(name = "meals") val meals: List<RecipeDetailDto>?
)


public fun RecipeListResponse.toRecipes() : List<Recipe> =
    meals.orEmpty().map { recipeDetailDto -> recipeDetailDto.toRecipe() }

@JsonClass(generateAdapter = true)
data class RecipeDetailResponse(
    @Json(name = "meals") val meals: List<RecipeDetailDto>?
)

fun RecipeDetailResponse.toRecipes() : List<Recipe> =
    meals.orEmpty().map { recipeDetailDto -> recipeDetailDto.toRecipe() }

@JsonClass(generateAdapter = true)
data class RecipeDto(
    @Json(name = "idMeal") val id: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strMealThumb") val thumbnail: String,
    @Json(name = "strCategory") val category: String?,
    @Json(name = "strArea") val area: String?
 )

@JsonClass(generateAdapter = true)
data class RecipeDetailDto(
    @Json(name = "idMeal") val id: String,
    @Json(name = "strMeal") val name: String,
    @Json(name = "strCategory") val category: String?,
    @Json(name = "strArea") val area: String?,
    @Json(name = "strInstructions") val instructions: String?,
    @Json(name = "strMealThumb") val imageUrl: String,
    @Json(name = "strIngredient1") val ingredient1: String?,
    @Json(name = "strIngredient2") val ingredient2: String?,
    @Json(name = "strIngredient3") val ingredient3: String?,
    @Json(name = "strIngredient4") val ingredient4: String?,
    @Json(name = "strIngredient5") val ingredient5: String?,
    @Json(name = "strMeasure1") val measure1: String?,
    @Json(name = "strMeasure2") val measure2: String?,
    @Json(name = "strMeasure3") val measure3: String?,
    @Json(name = "strMeasure4") val measure4: String?,
    @Json(name = "strMeasure5") val measure5: String?,
    @Json(name = "strYoutube") val youtubeUrl: String?,
    @Json(name = "strTags") val strTags: String?

) {
    fun getIngredients(): List<Ingredient> {
        var res = mutableListOf<Ingredient>()
        val ingredients = listOfNotNull(
            ingredient1, ingredient2, ingredient3, ingredient4, ingredient5
        ).filter { it.isNotBlank() }
        val measures = listOfNotNull(
            measure1, measure2, measure3, measure4, measure5
        ).filter { it.isNotBlank() }

        for ((index, ingredient) in ingredients.withIndex()) {
            res.add(Ingredient(ingredient, measures[index]))
        }
        return res
    }

    val tags get() =  strTags.orEmpty().split(",")
}

fun RecipeDetailDto.toRecipe(): Recipe = Recipe(
    id,
    name,
    imageUrl,
    category = category.orEmpty(),
    area = area.orEmpty(),
    instructions = instructions.orEmpty(),
    ingredients = this.getIngredients(),
    youtubeUrl,
    tags,
    isFavorite = false
)
