package com.example.hungerkiller.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hungerkiller.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val repository: MealRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val recipeId: String = savedStateHandle.get<String>("recipeId") ?: ""

    private val _uiState = MutableStateFlow<RecipeDetailUiState>(RecipeDetailUiState.Loading)
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        loadRecipe()
    }

    private fun loadRecipe() {
        viewModelScope.launch {
            _uiState.value = RecipeDetailUiState.Loading
            try {
                val recipe = repository.getRecipeById(recipeId)
                if (recipe != null) {
                    _uiState.value = RecipeDetailUiState.Success(recipe)
                } else {
                    _uiState.value = RecipeDetailUiState.Error("Recipe not found")
                }
            } catch (e: Exception) {
                _uiState.value = RecipeDetailUiState.Error(
                    e.message ?: "Failed to load recipe"
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState is RecipeDetailUiState.Success) {
            viewModelScope.launch {
                val newFavoriteStatus = !currentState.recipe.isFavorite
                repository.toggleFavorite(recipeId, newFavoriteStatus)
                _uiState.value = RecipeDetailUiState.Success(
                    currentState.recipe.copy(isFavorite = newFavoriteStatus)
                )
            }
        }
    }
}
