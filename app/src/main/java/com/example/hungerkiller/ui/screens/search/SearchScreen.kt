package com.example.hungerkiller.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hungerkiller.domain.model.Recipe
import com.example.hungerkiller.ui.components.RecipeCard

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    SearchContent(
        uiState = uiState,
        onExpandedChange = viewModel::onExpandedChange,
        onSearch = { query->  viewModel.onSearch(query)},
        onRecipeClick = onRecipeClick,
        onFavoriteClick = { recipe ->
            viewModel.toggleFavorite(recipe.id, recipe.isFavorite)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchContent(
    uiState: SearchUiSate,
    onExpandedChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (Recipe) -> Unit
) {
    val textFieldState = rememberTextFieldState()
    
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when (uiState) {
            is SearchUiSate.Success -> {
                SearchBar(
                    inputField = {
                        SearchBarDefaults.InputField(
                            state = textFieldState,
                            onSearch = { query ->
                                println("SearchScreen: onSearch triggered with query: '$query'")
                                onSearch(textFieldState.text.toString())
                            },
                            expanded = uiState.searchBarExpanded,
                            onExpandedChange = onExpandedChange,
                            placeholder = { Text("Search recipes") },
                        )
                    },
                    expanded = uiState.searchBarExpanded,
                    onExpandedChange = onExpandedChange,
                ) {
                    when(val searchState = uiState.searchState) {
                        SearchState.Loading -> {
                            Text(
                                text = "Searching...",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        is SearchState.Success -> {
                            if (searchState.recipes.isEmpty()) {
                                Text(
                                    text = "No recipes found",
                                    modifier = Modifier.padding(16.dp)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(searchState.recipes) { recipe ->
                                        RecipeCard(
                                            recipe = recipe,
                                            onClick = { onRecipeClick(recipe.id) },
                                            onFavoriteClick = { onFavoriteClick(recipe) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            is SearchUiSate.Loading -> {
                Text("Loading...")
            }
            is SearchUiSate.Error -> {
                Text("Error: ${uiState.message}")
            }
        }
    }
}