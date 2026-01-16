package com.example.hungerkiller.di

import com.example.hungerkiller.data.local.dao.RecipeDao
import com.example.hungerkiller.data.remote.api.MealDbApi
import com.example.hungerkiller.data.repository.MealRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMealRepository(
        mealDbApi: MealDbApi,
        recipeDao: RecipeDao
    ): MealRepository {
        return MealRepository(mealDbApi, recipeDao)
    }
}
