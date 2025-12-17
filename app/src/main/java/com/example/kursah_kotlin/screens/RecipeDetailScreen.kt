package com.example.kursah_kotlin.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kursah_kotlin.viewmodel.RecipeDetailViewModel


@Preview
@Composable
fun RecipeDetailScreen(
    onBackClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {},
    recipeId: String = "",
    viewModel: RecipeDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(recipeId) {
        viewModel.load(recipeId)
    }

    val recipe = uiState.recipe

    val title = recipe?.title ?: "Рецепт"
    val timeText = recipe?.timeMinutes?.let { "$it мин" } ?: "—"
    val difficulty = recipe?.difficulty ?: "Не указано"
    val category = recipe?.category ?: "Без категории"
    val diets = recipe?.tags.orEmpty()
    val description = recipe?.description ?: recipe?.steps?.firstOrNull() ?: "Описание недоступно"
    val ingredients = recipe?.ingredients ?: emptyList()
    val steps = recipe?.steps ?: emptyList()
    val ratingValue = recipe?.rating ?: 0.0
    val imageUrl = recipe?.imageUrl

    var isIngredientsExpanded by remember { mutableStateOf(true) }
    var isStepsExpanded by remember { mutableStateOf(true) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "home",
                onTabSelected = onNavigationClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(
                            color = Color(239,239,239,100),
                            shape = CircleShape
                        )
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад",
                        tint = Color.Black,
                    )
                }
                Text(
                    text = "Рецепт",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    ),
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "★",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 18.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = if (ratingValue > 0) ratingValue.toString() else "0.0",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoChip(
                    icon = Icons.Outlined.Timer,
                    text = timeText
                )
                InfoChip(
                    icon = Icons.Default.Timeline,
                    text = difficulty
                )
                InfoChip(
                    icon = Icons.Default.AutoAwesome,
                    text = category
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(220, 220, 220), RoundedCornerShape(12.dp))
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(model = imageUrl),
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(0.dp),
                        contentScale = ContentScale.Crop
                    )
                }

                Icon(
                    imageVector = if (uiState.isFavorite) Icons.Outlined.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (uiState.isFavorite) "Убрать из избранного" else "Добавить в избранное",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable {
                            if (recipeId.isNotBlank()) {
                                viewModel.toggleFavorite(recipeId) {
                                    onBookmarkClick()
                                }
                            }
                        }
                )

            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Описание")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeaderWithArrow(
                title = "Ингредиенты",
                isExpanded = isIngredientsExpanded,
                onToggle = { isIngredientsExpanded = !isIngredientsExpanded }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isIngredientsExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (ingredients.isEmpty()) {
                        Text(
                            text = "Нет данных",
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        )
                    } else {
                        ingredients.forEach { amountDto ->
                            val name = amountDto.ingredient?.name
                            val amount = amountDto.amount?.let { amt ->
                                val unit = amountDto.unit ?: ""
                                "$amt $unit".trim()
                            } ?: ""
                            Text(
                                text = listOfNotNull(name, amount.takeIf { it.isNotEmpty() }?.let { " - $it" }).joinToString(""),
                                style = TextStyle(
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeaderWithArrow(
                title = "Приготовление",
                isExpanded = isStepsExpanded,
                onToggle = { isStepsExpanded = !isStepsExpanded }
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (isStepsExpanded) {
                if (steps.isEmpty()) {
                    Text(
                        text = "Нет данных",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        steps.forEachIndexed { index, step ->
                            Text(
                                text = "${index + 1}. $step",
                                style = TextStyle(
                                    fontFamily = PlayfairDisplayFontFamily,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun InfoChip(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 12.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(
            fontFamily = PlayfairDisplayFontFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    )
}

@Composable
private fun SectionHeaderWithArrow(
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(title)
        Icon(
            imageVector = Icons.Outlined.ArrowDownward,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier
                .size(20.dp)
                .rotate(if (isExpanded) 180f else 0f)
        )
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(230, 230, 230))
    )
}