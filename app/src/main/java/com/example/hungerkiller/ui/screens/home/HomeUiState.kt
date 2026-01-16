package com.example.hungerkiller.ui.screens.home

import com.example.hungerkiller.domain.model.Recipe


sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val recipes: List<Recipe>, val isOffline: Boolean = false) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
