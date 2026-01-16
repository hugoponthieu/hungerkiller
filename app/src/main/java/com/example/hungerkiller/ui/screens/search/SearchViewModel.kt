package com.example.hungerkiller.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hungerkiller.data.repository.MealRepository
import com.example.hungerkiller.ui.screens.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val mealRepository: MealRepository
    ) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiSate>(
        SearchUiSate.Success(
            searchBarExpanded = false,
            searchState = SearchState.Success(recipes = emptyList())
        )
    )

    init {
        onSearch("chick")
    }
    val uiState: StateFlow<SearchUiSate> = _uiState.asStateFlow()

    fun onExpandedChange(expanded: Boolean) {
        val currentState = _uiState.value
        if (currentState is SearchUiSate.Success) {
            _uiState.value = currentState.copy(searchBarExpanded = expanded)
        }
    }

     fun onSearch(query: String) {
         Log.d("SearchViewModel", "onSearch called with query: '$query'")
         viewModelScope.launch {
             val currentState = _uiState.value
             Log.d("SearchViewModel", "Current state: $currentState")
             if (currentState is SearchUiSate.Success) {
                 _uiState.value = SearchUiSate.Loading
                 try {
                     Log.d("SearchViewModel", "Searching for: '$query'")
                     val resMeal = mealRepository.searchRecipes(query)
                     Log.i("SearchViewModel","Search triggered, found ${resMeal.size} recipes")
                     _uiState.value = currentState.copy(searchState = SearchState.Success(resMeal))
                 } catch (e: Exception) {
                     Log.e("SearchViewModel", "Search error", e)
                     _uiState.value = SearchUiSate.Error(e.message ?: "Unknown error")
                 }
             }
         }
    }

    fun toggleFavorite(recipeId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            mealRepository.toggleFavorite(recipeId, !isFavorite)
            // Update the UI state with the new favorite status
            val currentState = _uiState.value
            if (currentState is SearchUiSate.Success) {
                val searchState = currentState.searchState
                if (searchState is SearchState.Success) {
                    val updatedRecipes = searchState.recipes.map { recipe ->
                        if (recipe.id == recipeId) {
                            recipe.copy(isFavorite = !isFavorite)
                        } else {
                            recipe
                        }
                    }
                    _uiState.value = currentState.copy(
                        searchState = SearchState.Success(updatedRecipes)
                    )
                }
            }
        }
    }
}