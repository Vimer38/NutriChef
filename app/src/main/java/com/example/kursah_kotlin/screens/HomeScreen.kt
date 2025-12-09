package com.example.kursah_kotlin.screens

import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.FilterAlt
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(
    userName: String = "Имя",
    onSearchClick: () -> Unit = {},
    onCategoryClick: (String) -> Unit = {},
    onRecipeClick: (String) -> Unit = {},
    onSeeAllClick: (String) -> Unit = {},
    onNavigationClick: (String) -> Unit = {}
) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }
    
    val categories = listOf("Все", "Супы", "Горячее", "Десерты", "Завтраки", "Обеды")
    val recommendations = listOf(
        RecipeCard("Название блюда", "Краткое описание", "30 мин"),
        RecipeCard("Название блюда", "Краткое описание", "30 мин"),
        RecipeCard("Название блюда", "Краткое описание", "30 мин")
    )
    val weeklyRecipes = listOf(
        RecipeCard("Название блюда", "Краткое описание", null),
        RecipeCard("Название блюда", "Краткое описание", null),
        RecipeCard("Название блюда", "Краткое описание", null)
    )

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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(238, 238, 238), CircleShape)
                )
                Column {
                    Text(
                        text = "Доброе утро",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    )
                    Text(
                        text = userName,
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Голодны?",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 25.sp,
                    color = Color.Black
                )
            )
            Text(
                text = "Что приготовим сегодня?",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 25.sp,
                    color = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            SearchBar(
                value = searchText,
                onValueChange = { searchText = it },
                onSearchClick = onSearchClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    CategoryChip(
                        text = category,
                        isSelected = category == selectedCategory,
                        onClick = {
                            selectedCategory = category
                            onCategoryClick(category)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(
                title = "Рекомендации",
                onSeeAllClick = { onSeeAllClick("Рекомендации") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(recommendations) { recipe ->
                    RecipeCardItem(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            SectionHeader(
                title = "Рецепты недели",
                onSeeAllClick = { onSeeAllClick("Рецепты недели") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(weeklyRecipes) { recipe ->
                    RecipeCardItem(
                        recipe = recipe,
                        onClick = { onRecipeClick(recipe.title) }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Найти рецепт",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = "Filter",
                tint = Color.Gray,
                modifier = Modifier.clickable { onSearchClick() }
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color(238, 238, 238),
            focusedContainerColor = Color(238, 238, 238),
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(30.dp),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontFamily = PlayfairDisplayFontFamily,
            fontSize = 16.sp
        )
    )
}

@Composable
fun CategoryChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isSelected) Color(238,238,238) else Color.White,
                shape = RoundedCornerShape(20.dp)
            )
            .then(
                if (!isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Color(193,190,190),
                        shape = RoundedCornerShape(20.dp)
                    )
                } else {
                    Modifier
                }
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 14.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
fun SectionHeader(
    title: String,
    onSeeAllClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 20.sp,
                color = Color.Black
            )
        )
        Text(
            text = "Смотреть все",
            modifier = Modifier.clickable { onSeeAllClick() },
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 14.sp,
                color = Color.Black
            )
        )
    }
}

data class RecipeCard(
    val title: String,
    val description: String,
    val time: String?
)

@Composable
fun RecipeCardItem(
    recipe: RecipeCard,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(width = 200.dp, height = 230.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(Color(238, 238, 238), RoundedCornerShape(12.dp))
        ) {
            if (recipe.time != null) {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(
                            Color.White.copy(alpha = 0.9f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = recipe.time,
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = recipe.title,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = recipe.description,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 14.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(210.dp,50.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(30.dp),
                    spotColor = Color.Black.copy(alpha = 0.15f)
                )
                .background(
                    color = Color(193,190,190),
                    shape = RoundedCornerShape(30.dp)
                ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavigationIcon(
                    icon = Icons.Outlined.Home,
                    isSelected = selectedTab == "home",
                    onClick = { onTabSelected("home") }
                )
                NavigationIcon(
                    icon = Icons.Outlined.Search,
                    isSelected = selectedTab == "search",
                    onClick = { onTabSelected("search") }
                )
                NavigationIcon(
                    icon = Icons.Default.BookmarkBorder,
                    isSelected = selectedTab == "bookmark",
                    onClick = { onTabSelected("bookmark") }
                )
                NavigationIcon(
                    icon = Icons.Outlined.Person,
                    isSelected = selectedTab == "profile",
                    onClick = { onTabSelected("profile") }
                )
            }
        }
    }
}

@Composable
fun NavigationIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color(60, 60, 60) 
        )
    }
}

