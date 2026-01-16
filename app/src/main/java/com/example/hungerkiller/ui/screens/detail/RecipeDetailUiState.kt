package com.example.hungerkiller.ui.screens.detail

import com.example.hungerkiller.domain.model.Recipe

sealed interface RecipeDetailUiState {
    data object Loading : RecipeDetailUiState
    data class Success(val recipe: Recipe) : RecipeDetailUiState
    data class Error(val message: String) : RecipeDetailUiState
}
