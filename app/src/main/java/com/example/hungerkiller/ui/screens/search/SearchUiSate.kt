package com.example.hungerkiller.ui.screens.search

import com.example.hungerkiller.domain.model.Recipe

sealed class SearchUiSate {
    data object Loading : SearchUiSate()
    data class Error(val message: String) : SearchUiSate()
    data class Success(
        val searchBarExpanded: Boolean,
        val searchState: SearchState
    ) : SearchUiSate()
}

sealed class SearchState {
    data object Loading: SearchState()
    data class Success( val recipes : List<Recipe>
    ): SearchState()
}