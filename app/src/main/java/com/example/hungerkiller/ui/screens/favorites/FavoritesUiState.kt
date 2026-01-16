package com.example.hungerkiller.ui.screens.favorites

import com.example.hungerkiller.domain.model.Recipe

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState
    data class Success(val favorites: List<Recipe>) : FavoritesUiState
    data class Error(val message: String) : FavoritesUiState
}
