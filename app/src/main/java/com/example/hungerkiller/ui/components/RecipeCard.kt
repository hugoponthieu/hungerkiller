package com.example.hungerkiller.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.hungerkiller.domain.model.Ingredient
import com.example.hungerkiller.domain.model.Recipe
import com.example.hungerkiller.ui.theme.HungerKillerTheme

/**
 * A card component that displays recipe information including image, name, category, and area.
 * 
 * @param recipe The recipe data to display
 * @param onClick Callback when the card is clicked
 * @param onFavoriteClick Callback when the favorite icon is clicked
 * @param modifier Optional modifier for the card
 */
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Recipe Image
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop
            )

            // Recipe Details
            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = recipe.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (recipe.category.isNotEmpty()) {
                        Text(
                            text = recipe.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (recipe.area.isNotEmpty()) {
                        Text(
                            text = "• ${recipe.area}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (recipe.tags.isNotEmpty()) {
                    Text(
                        text = recipe.tags.take(3).joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Favorite Icon
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                    tint = if (recipe.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Compact version of RecipeCard for grid layouts.
 */
@Composable
fun RecipeCardCompact(
    recipe: Recipe,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Recipe Image
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = recipe.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onFavoriteClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (recipe.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (recipe.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (recipe.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (recipe.category.isNotEmpty()) {
                    Text(
                        text = recipe.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
private fun RecipeCardPreview() {
    HungerKillerTheme {
        RecipeCard(
            recipe = Recipe(
                id = "1",
                name = "Spaghetti Carbonara",
                imageUrl = "https://www.themealdb.com/images/media/meals/llcbn01574260722.jpg",
                category = "Pasta",
                area = "Italian",
                instructions = "Cook pasta...",
                ingredients = listOf(
                    Ingredient("Spaghetti", "200g"),
                    Ingredient("Eggs", "2")
                ),
                youtubeUrl = null,
                tags = listOf("Pasta", "Italian", "Quick"),
                isFavorite = false
            ),
            onClick = {},
            onFavoriteClick = {}
        )
    }
}

@Preview(showBackground = true, widthDp = 200)
@Composable
private fun RecipeCardCompactPreview() {
    HungerKillerTheme {
        RecipeCardCompact(
            recipe = Recipe(
                id = "1",
                name = "Spaghetti Carbonara",
                imageUrl = "https://www.themealdb.com/images/media/meals/llcbn01574260722.jpg",
                category = "Pasta",
                area = "Italian",
                instructions = "Cook pasta...",
                ingredients = listOf(
                    Ingredient("Spaghetti", "200g"),
                    Ingredient("Eggs", "2")
                ),
                youtubeUrl = null,
                tags = listOf("Pasta", "Italian"),
                isFavorite = true
            ),
            onClick = {},
            onFavoriteClick = {}
        )
    }
}
