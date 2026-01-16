package com.example.hungerkiller.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hungerkiller.data.repository.MealRepository
import com.example.hungerkiller.data.repository.MealRepositoryError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mealRepository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeRecipes()
    }

    private fun loadHomeRecipes() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                // Try to get random recipe from API
                val recipes = mealRepository.getRandom()
                _uiState.value = HomeUiState.Success(recipes, isOffline = false)
            } catch (e: Exception) {
                // If API fails, show cached recipes
                val cachedRecipes = mealRepository.getAllRecipesList().take(5)
                if (cachedRecipes.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(cachedRecipes, isOffline = true)
                } else {
                    _uiState.value = HomeUiState.Error(
                        "Unable to connect. Please check your internet connection."
                    )
                }
            }
        }
    }

    fun searchRecipes(query: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val recipes = mealRepository.searchRecipes(query)
                _uiState.value = HomeUiState.Success(recipes)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getRandomRecipe() {
        loadHomeRecipes()
    }

    fun toggleFavorite(recipeId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            mealRepository.toggleFavorite(recipeId, !isFavorite)
            // Update the UI state with the new favorite status
            val currentState = _uiState.value
            if (currentState is HomeUiState.Success) {
                val updatedRecipes = currentState.recipes.map { recipe ->
                    if (recipe.id == recipeId) {
                        recipe.copy(isFavorite = !isFavorite)
                    } else {
                        recipe
                    }
                }
                _uiState.value = HomeUiState.Success(updatedRecipes, currentState.isOffline)
            }
        }
    }
}
