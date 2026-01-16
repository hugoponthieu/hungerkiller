package com.example.hungerkiller.data.local.dao

import androidx.room.*
import com.example.hungerkiller.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    suspend fun getRecipeById(recipeId: String): RecipeEntity?
    
    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeByIdFlow(recipeId: String): Flow<RecipeEntity?>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY createdAt DESC")
    suspend fun getFavoriteRecipesList(): List<RecipeEntity>
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR area LIKE '%' || :query || '%'")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' OR area LIKE '%' || :query || '%'")
    suspend fun searchRecipesList(query: String): List<RecipeEntity>
    
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>
    
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    suspend fun getAllRecipesList(): List<RecipeEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipe(recipe: RecipeEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipes: List<RecipeEntity>)
    
    @Update
    suspend fun updateRecipe(recipe: RecipeEntity)
    
    @Query("UPDATE recipes SET isFavorite = :isFavorite WHERE id = :recipeId")
    suspend fun updateFavoriteStatus(recipeId: String, isFavorite: Boolean)
    
    @Delete
    suspend fun deleteRecipe(recipe: RecipeEntity)
    
    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: String)
    
    @Query("DELETE FROM recipes WHERE isFavorite = 0")
    suspend fun deleteNonFavorites()
}
