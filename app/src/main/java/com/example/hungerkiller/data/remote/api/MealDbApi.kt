package com.example.hungerkiller.data.remote.api

import com.example.hungerkiller.data.remote.dto.RecipeListResponse
import com.example.hungerkiller.data.remote.dto.RecipeDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MealDbApi {

    @GET("search.php")
    suspend fun searchRecipes(
        @Query("s") query: String
    ): Response<RecipeListResponse>

    @GET("lookup.php")
    suspend fun getRecipeById(
        @Query("i") id: String
    ): Response<RecipeDetailResponse>

    @GET("filter.php")
    suspend fun getRecipesByCategory(
        @Query("c") category: String
    ): Response<RecipeListResponse>

    @GET("filter.php")
    suspend fun getRecipesByArea(
        @Query("a") area: String
    ): Response<RecipeListResponse>

    @GET("random.php")
    suspend fun getRandomRecipe(): Response<RecipeDetailResponse>

    @GET("list.php?c=list")
    suspend fun getCategories(): Response<RecipeListResponse>

    @GET("list.php?a=list")
    suspend fun getAreas(): Response<RecipeListResponse>
}
