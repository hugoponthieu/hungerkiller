package com.example.hungerkiller.data.repository

import android.util.Log
import com.example.hungerkiller.data.local.dao.RecipeDao
import com.example.hungerkiller.data.local.entity.toEntity
import com.example.hungerkiller.data.local.entity.toRecipe
import com.example.hungerkiller.data.remote.api.MealDbApi
import com.example.hungerkiller.data.remote.dto.toRecipes
import com.example.hungerkiller.domain.model.Recipe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class MealRepository @Inject constructor(
    private val mealDbApi: MealDbApi,
    private val recipeDao: RecipeDao
) {

    // Offline-first: Try to fetch from API, cache results, fallback to local DB
    suspend fun searchRecipes(query: String): List<Recipe> {
        return try {
            // Try to fetch from API
            val response = mealDbApi.searchRecipes(query)
            if (response.isSuccessful) {
                val recipes = response.body()?.toRecipes() ?: emptyList()
                // Cache the results
                recipeDao.insertRecipes(recipes.map { it.toEntity() })
                recipes
            } else {
                // Fallback to local search
                recipeDao.searchRecipesList(query).map { it.toRecipe() }
            }
        } catch (e: Exception) {
            Log.e("SEARCH_RECIPES", "Error fetching from API, using local: ${e.message}")
            // Fallback to local search if API fails
            recipeDao.searchRecipesList(query).map { it.toRecipe() }
        }
    }

    suspend fun getRandom(): List<Recipe> {
        return try {
            val response = mealDbApi.getRandomRecipe()
            if (response.isSuccessful) {
                val recipes = response.body()?.toRecipes() ?: emptyList()
                // Cache the random recipe
                recipeDao.insertRecipes(recipes.map { it.toEntity() })
                recipes
            } else {
                throw MealRepositoryError(MealRepoErrorCode.NOT_FOUND)
            }
        } catch (e: Exception) {
            Log.e("RANDOM_FETCH", e.message.orEmpty())
            throw MealRepositoryError(MealRepoErrorCode.SERVER_ERROR)
        }
    }

    suspend fun getRecipeById(id: String): Recipe? {
        // Try local first
        val localRecipe = recipeDao.getRecipeById(id)
        if (localRecipe != null) {
            return localRecipe.toRecipe()
        }

        // If not in local, try API
        return try {
            val response = mealDbApi.getRecipeById(id)
            if (response.isSuccessful) {
                val recipes = response.body()?.toRecipes() ?: emptyList()
                val recipe = recipes.firstOrNull()
                recipe?.let {
                    recipeDao.insertRecipe(it.toEntity())
                }
                recipe
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GET_RECIPE", "Error fetching recipe: ${e.message}")
            null
        }
    }

    // Favorites management
    fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    suspend fun toggleFavorite(recipeId: String, isFavorite: Boolean) {
        recipeDao.updateFavoriteStatus(recipeId, isFavorite)
    }

    suspend fun addToFavorites(recipe: Recipe) {
        val entity = recipe.copy(isFavorite = true).toEntity()
        recipeDao.insertRecipe(entity)
    }

    suspend fun removeFromFavorites(recipeId: String) {
        recipeDao.updateFavoriteStatus(recipeId, false)
    }

    // Search in local database
    fun searchLocalRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    // Get all cached recipes
    fun getAllCachedRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.map { it.toRecipe() }
        }
    }

    suspend fun getAllRecipesList(): List<Recipe> {
        return recipeDao.getAllRecipesList().map { it.toRecipe() }
    }
}

class MealRepositoryError(val code: MealRepoErrorCode) : Exception(code.name)

enum class MealRepoErrorCode(string: String) {
    NOT_FOUND("Could not find repo"),
    SERVER_ERROR("Could not retrieve meal from server")
}