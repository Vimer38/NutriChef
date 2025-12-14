package com.example.kursah_kotlin.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedRecipesScreen(
    onBackClick: () -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    onViewAllClick: (String) -> Unit = {},
    onNavigationClick: (String) -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var expandedCategories by remember { mutableStateOf(setOf<String>()) }
    var categories by remember { mutableStateOf<List<Pair<String, List<RecipeCard>>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Загружаем избранные рецепты из базы данных
    androidx.compose.runtime.LaunchedEffect(Unit) {
        kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val db = com.example.kursah_kotlin.data.local.DatabaseProvider.getDatabase(context)
                val favoriteEntities = db.recipeDao().getFavorites()
                
                // Загружаем полные данные рецептов из JSON
                val jsonString = context.assets.open("recipes.json").bufferedReader().use { it.readText() }
                val type = object : com.google.gson.reflect.TypeToken<List<com.example.kursah_kotlin.data.remote.dto.RecipeDto>>() {}.type
                val allRecipes = com.google.gson.Gson().fromJson<List<com.example.kursah_kotlin.data.remote.dto.RecipeDto>>(jsonString, type) ?: emptyList()
                
                // Фильтруем только избранные
                val favoriteIds = favoriteEntities.map { it.id }.toSet()
                val favorites = allRecipes.filter { favoriteIds.contains(it.id) }
                
                // Группируем по категориям
                val grouped = favorites
                    .groupBy { it.category ?: "Без категории" }
                    .map { (category, recipes) ->
                        category to recipes.map { recipe ->
                            RecipeCard(
                                id = recipe.id ?: "",
                                title = recipe.title ?: "Без названия",
                                description = recipe.description ?: "Нет описания",
                                time = recipe.timeMinutes?.let { "$it мин" }
                            )
                        }
                    }
                
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    categories = grouped
                    isLoading = false
                    // Раскрываем первую категорию
                    if (grouped.isNotEmpty()) {
                        expandedCategories = setOf(grouped.first().first)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(238, 238, 238), RoundedCornerShape(20.dp))
                            .clickable { onBackClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Назад",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                },
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Сохраненные рецепты",
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = Color.Black,
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "bookmark",
                onTabSelected = onNavigationClick
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Загрузка...",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                }
            }
            categories.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Text(
                            text = "Нет сохраненных рецептов",
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        )
                        Text(
                            text = "Добавьте рецепты в избранное",
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontSize = 14.sp,
                                color = Color.LightGray
                            )
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                ) {
                    categories.forEachIndexed { index, (category, recipes) ->
                        item {
                            CategorySection(
                                category = category,
                                recipes = recipes,
                                isExpanded = expandedCategories.contains(category),
                                onToggleExpand = {
                                    expandedCategories = if (expandedCategories.contains(category)) {
                                        expandedCategories - category
                                    } else {
                                        expandedCategories + category
                                    }
                                },
                                onRecipeClick = onRecipeClick,
                                onViewAllClick = { onViewAllClick(category) }
                            )
                            if (index < categories.size - 1) {
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CategorySection(
    category: String,
    recipes: List<RecipeCard>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onRecipeClick: (String) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleExpand() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )
            Icon(
                imageVector = Icons.Outlined.ArrowDownward,
                contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                modifier = Modifier.size(24.dp),
                tint = Color.Black
            )
        }

        if (isExpanded && recipes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                recipes.chunked(2).forEach { pair ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        pair.forEach { recipe ->
                            SavedRecipeCardItem(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.id) },
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Button(
                    onClick = onViewAllClick,
                    modifier = Modifier
                        .width(150.dp)
                        .height(46.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(193,190,190,100)
                    )
                ) {
                    Text(
                        text = "Смотреть все",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 13.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(238, 238, 238))
        )
    }
}

@Composable
fun SimpleWhiteFade() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.7f),
                        Color.White.copy(alpha = 0.9f),
                        Color.White
                    )
                )
            )
    )
}

@Composable
fun SavedRecipeCardItem(
    recipe: RecipeCard,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(width = 180.dp, height = 240.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Color(238, 238, 238), RoundedCornerShape(12.dp))
        ) {
            Icon(
                imageVector = Icons.Outlined.Bookmark,
                contentDescription = "В закладках",
                modifier = Modifier
                    .padding(12.dp)
                    .size(20.dp),
                tint = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recipe.title,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = recipe.description,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 10.sp,
                color = Color.Black
            )
        )
    }
}

