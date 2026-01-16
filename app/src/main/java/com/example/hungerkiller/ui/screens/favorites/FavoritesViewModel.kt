package com.example.hungerkiller.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hungerkiller.data.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            repository.getFavoriteRecipes()
                .catch { e ->
                    _uiState.value = FavoritesUiState.Error(
                        e.message ?: "Failed to load favorites"
                    )
                }
                .collect { favorites ->
                    _uiState.value = FavoritesUiState.Success(favorites)
                }
        }
    }

    fun removeFromFavorites(recipeId: String) {
        viewModelScope.launch {
            repository.removeFromFavorites(recipeId)
        }
    }

    fun refresh() {
        loadFavorites()
    }
}
