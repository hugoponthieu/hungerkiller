package com.example.hungerkiller.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.hungerkiller.ui.components.RecipeCard

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onRecipeClick: (String) -> Unit = {},
    onNavigateToSearch: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HomeContent(
        uiState = uiState,
        onSearchRecipes = { query -> viewModel.searchRecipes(query) },
        onRandomRecipe = { viewModel.getRandomRecipe() },
        onRecipeClick = onRecipeClick,
        onFavoriteClick = { recipe ->
            viewModel.toggleFavorite(recipe.id, recipe.isFavorite)
        },
        onNavigateToSearch = onNavigateToSearch
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onSearchRecipes: (String) -> Unit,
    onRandomRecipe: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (com.example.hungerkiller.domain.model.Recipe) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with title and refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Discover Recipes",
                    style = MaterialTheme.typography.headlineMedium
                )
                
                // Show offline badge if applicable
                if (uiState is HomeUiState.Success && uiState.isOffline) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Offline",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Offline Mode",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            
            IconButton(onClick = onRandomRecipe) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Get Random Recipe"
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtitle
        Text(
            if (uiState is HomeUiState.Success && uiState.isOffline) {
                "Showing recent recipes from cache"
            } else {
                "Random recipe of the day"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Quick search links
        QuickSearchSection(onSearch = { query ->
            onSearchRecipes(query)
        }, onNavigateToSearch = onNavigateToSearch)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when (uiState) {
            is HomeUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Loading delicious recipes...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
            is HomeUiState.Success -> {
                if (uiState.recipes.isEmpty()) {
                    EmptyStateMessage(
                        onNavigateToSearch = onNavigateToSearch,
                        onTryAgain = onRandomRecipe
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.recipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                                onFavoriteClick = { onFavoriteClick(recipe) }
                            )
                        }
                    }
                }
            }
            is HomeUiState.Error -> {
                OfflineErrorState(
                    message = uiState.message,
                    onNavigateToSearch = onNavigateToSearch,
                    onTryAgain = onRandomRecipe
                )
            }
        }
    }
}

@Composable
private fun QuickSearchSection(
    onSearch: (String) -> Unit,
    onNavigateToSearch: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Quick Search",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            
            TextButton(onClick = onNavigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("View All")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickSearchItems) { item ->
                SuggestionChip(
                    onClick = { onNavigateToSearch() },
                    label = { Text(item.label) },
                    icon = {
                        Text(
                            item.emoji,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun OfflineErrorState(
    message: String,
    onNavigateToSearch: () -> Unit,
    onTryAgain: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "No Internet Connection",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "You're offline. Try searching for recipes you've viewed before, or connect to the internet for fresh content.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateToSearch,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Browse Cache")
                    }
                    
                    Button(
                        onClick = onTryAgain,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Try Again")
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    onNavigateToSearch: () -> Unit,
    onTryAgain: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                "🍽️",
                style = TextStyle(fontSize = 64.sp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                "No Recipes Found",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Start by searching for your favorite dishes",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(onClick = onNavigateToSearch) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search Recipes")
            }
        }
    }
}

private data class QuickSearchItem(val label: String, val emoji: String, val query: String)

private val quickSearchItems = listOf(
    QuickSearchItem("Chicken", "🍗", "chicken"),
    QuickSearchItem("Pasta", "🍝", "pasta"),
    QuickSearchItem("Seafood", "🦐", "seafood"),
    QuickSearchItem("Vegetarian", "🥗", "vegetarian"),
    QuickSearchItem("Dessert", "🍰", "dessert"),
    QuickSearchItem("Beef", "🥩", "beef"),
)