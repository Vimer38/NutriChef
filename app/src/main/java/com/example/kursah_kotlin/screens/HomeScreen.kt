package com.example.kursah_kotlin.screens

import android.graphics.Paint
import android.net.Uri
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kursah_kotlin.data.local.DatabaseProvider
import com.example.kursah_kotlin.data.remote.dto.RecipeDto
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.draw.clip
import java.io.IOException

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
    val context = LocalContext.current

    val database = remember { DatabaseProvider.getDatabase(context) }
    val userRepository = remember { UserRepositoryImpl(database) }
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }

    var firstName by remember { mutableStateOf<String?>(null) }
    var photoPath by remember { mutableStateOf<String?>(null) }

    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Все") }
    var showAllRecommendations by remember { mutableStateOf(false) }
    var showAllWeekly by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    var appliedTimeRange by remember { mutableStateOf(0f..120f) }
    var appliedDifficulties by remember { mutableStateOf(emptyList<String>()) }
    var appliedTags by remember { mutableStateOf(emptyList<String>()) }
    var appliedCaloriesRange by remember { mutableStateOf(0f..1000f) }
    var appliedProteinRange by remember { mutableStateOf(0f..100f) }
    var appliedFatRange by remember { mutableStateOf(0f..100f) }
    var appliedCarbsRange by remember { mutableStateOf(0f..200f) }
    var allRecipes by remember { mutableStateOf<List<RecipeCard>>(emptyList()) }

    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    if (showFilterSheet) {
        FilterBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState,
            initialTimeRange = appliedTimeRange,
            initialDifficulties = appliedDifficulties,
            initialTags = appliedTags,
            initialCaloriesRange = appliedCaloriesRange,
            initialProteinRange = appliedProteinRange,
            initialFatRange = appliedFatRange,
            initialCarbsRange = appliedCarbsRange,
            onApply = { time, diffs, tags, calories, protein, fat, carbs ->
                appliedTimeRange = time
                appliedDifficulties = diffs
                appliedTags = tags
                appliedCaloriesRange = calories
                appliedProteinRange = protein
                appliedFatRange = fat
                appliedCarbsRange = carbs
                showFilterSheet = false
            }
        )
    }

    LaunchedEffect(currentUser?.uid) {
        currentUser?.let { user ->
            val profile = userRepository.getUserProfile(user.uid)
            firstName = profile?.firstName ?: userName
            photoPath = profile?.photoPath
        } ?: run {
            firstName = userName
            photoPath = null
        }

        val (cards, error) = loadRecipesFromAssets(context)
        if (cards.isNotEmpty()) {
            allRecipes = cards
        }
        isLoading = false
        if (error != null) {
            error.printStackTrace()
        }
    }

    val filteredRecipes = remember(allRecipes, searchText, selectedCategory, appliedTimeRange, appliedDifficulties, appliedTags, appliedCaloriesRange, appliedProteinRange, appliedFatRange, appliedCarbsRange) {
        allRecipes.filter { recipe ->
            val matchesSearch = recipe.title.contains(searchText, ignoreCase = true)
            val matchesCategory = selectedCategory == "Все" || recipe.category == selectedCategory
            
            val time = recipe.time?.replace(" мин", "")?.toIntOrNull() ?: 0
            val matchesTime = time >= appliedTimeRange.start && time <= appliedTimeRange.endInclusive
            
            val matchesDifficulty = appliedDifficulties.isEmpty() || appliedDifficulties.contains(recipe.difficulty)
            
            val recipeTags = recipe.diets
            val matchesTags = appliedTags.isEmpty() || appliedTags.all { it in recipeTags }

            val matchesCalories = recipe.calories?.let {
                it >= appliedCaloriesRange.start && it <= appliedCaloriesRange.endInclusive 
            } ?: true
            val matchesProtein = recipe.protein?.let { 
                it >= appliedProteinRange.start && it <= appliedProteinRange.endInclusive 
            } ?: true
            val matchesFat = recipe.fat?.let { 
                it >= appliedFatRange.start && it <= appliedFatRange.endInclusive 
            } ?: true
            val matchesCarbs = recipe.carbs?.let { 
                it >= appliedCarbsRange.start && it <= appliedCarbsRange.endInclusive 
            } ?: true

            matchesSearch && matchesCategory && matchesTime && matchesDifficulty && matchesTags && matchesCalories && matchesProtein && matchesFat && matchesCarbs
        }
    }

    val recommendations = remember(filteredRecipes) {
         filteredRecipes.filter { !it.isRecipeOfWeek }.take(10)
    }
    val weeklyRecipes = remember(filteredRecipes) {
        filteredRecipes.filter { it.isRecipeOfWeek }.take(10)
    }

    val categories = listOf("Все", "Супы", "Горячее", "Десерты", "Завтраки", "Обеды")

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedTab = "home",
                onTabSelected = onNavigationClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(238, 238, 238), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoPath.isNullOrBlank()) {
                            Image(
                                painter = rememberAsyncImagePainter(model = Uri.parse(photoPath!!)),
                                contentDescription = "Аватар",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
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
                            text = firstName.toString(),
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
            item {
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
            }
            item {
                SearchBar(
                    value = searchText,
                    onValueChange = { searchText = it },
                    onSearchClick = onSearchClick,
                    onFilterClick = { showFilterSheet = true }
                )
            }
            item {
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
            }
            item {
                SectionHeader(
                    title = "Рекомендации",
                    isExpanded = showAllRecommendations,
                    onSeeAllClick = {
                        showAllRecommendations = !showAllRecommendations
                        onSeeAllClick("Рекомендации")
                    }
                )
            }
            if (isLoading) {
                item {
                    Text(
                        text = "Загрузка рецептов...",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )
                }
            } else {
                if (showAllRecommendations) {
                    items(recommendations) { recipe ->
                        RecipeCardItem(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(recommendations.take(5)) { recipe ->
                                RecipeCardItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    }
                }
            }
            item {
                SectionHeader(
                    title = "Рецепты недели",
                    isExpanded = showAllWeekly,
                    onSeeAllClick = {
                        showAllWeekly = !showAllWeekly
                        onSeeAllClick("Рецепты недели")
                    }
                )
            }
            if (!isLoading) {
                if (showAllWeekly) {
                    items(weeklyRecipes) { recipe ->
                        RecipeCardItem(
                            recipe = recipe,
                            onClick = { onRecipeClick(recipe.id) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(weeklyRecipes.take(5)) { recipe ->
                                RecipeCardItem(
                                    recipe = recipe,
                                    onClick = { onRecipeClick(recipe.id) }
                                )
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

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
                contentDescription = "Поиск",
                tint = Color.Gray
            )
        },
        trailingIcon = {
            Icon(
                imageVector = Icons.Outlined.FilterAlt,
                contentDescription = "Фильтр",
                tint = Color.Gray,
                modifier = Modifier.clickable { onFilterClick() }
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
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                onSearchClick()
            }
        ),
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
    isExpanded: Boolean = false,
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
            text = if (isExpanded) "Свернуть" else "Смотреть все",
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
    val id: String,
    val title: String,
    val description: String,
    val time: String?,
    val category: String? = null,
    val diets: List<String> = emptyList(),
    val difficulty: String? = null,
    val imageUrl: String? = null,
    val rating: Double? = null,
    val isRecipeOfWeek: Boolean = false,
    val calories: Int? = null,
    val protein: Double? = null,
    val fat: Double? = null,
    val carbs: Double? = null
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
            if (!recipe.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = recipe.imageUrl),
                    contentDescription = recipe.title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

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
        val meta = listOfNotNull(
            recipe.category,
            recipe.difficulty,
            recipe.diets.takeIf { it.isNotEmpty() }?.joinToString(", ")
        ).joinToString(" • ")
        if (meta.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meta,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

private suspend fun loadRecipesFromAssets(
    context: android.content.Context
): Pair<List<RecipeCard>, Exception?> {
    return try {
        val jsonString = withContext(Dispatchers.IO) {
            context.assets.open("recipes.json")
                .bufferedReader()
                .use { it.readText() }
        }
        if (jsonString.isEmpty()) {
            return Pair(emptyList(), IOException("recipes.json пустой"))
        }
        val type = object : TypeToken<List<RecipeDto>>() {}.type
        val recipes = Gson().fromJson<List<RecipeDto>>(jsonString, type) ?: emptyList()
        val cards = recipes.mapNotNull { dto ->
            val id = dto.id ?: return@mapNotNull null
            val title = dto.title ?: return@mapNotNull null
            RecipeCard(
                id = id,
                title = title,
                description = dto.description ?: dto.nutrients?.calories?.let { "$it ккал" } ?: "Рецепт",
                time = dto.timeMinutes?.let { "$it мин" },
                category = dto.category,
                diets = dto.tags,
                difficulty = dto.difficulty,
                imageUrl = dto.imageUrl,
                rating = dto.rating,
                isRecipeOfWeek = dto.isRecipeOfWeek,
                calories = dto.nutrients?.calories,
                protein = dto.nutrients?.protein,
                fat = dto.nutrients?.fat,
                carbs = dto.nutrients?.carbs
            )
        }
        Pair(cards, null)
    } catch (e: Exception) {
        Pair(emptyList(), e)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    initialTimeRange: ClosedFloatingPointRange<Float>,
    initialDifficulties: List<String>,
    initialTags: List<String>,
    initialCaloriesRange: ClosedFloatingPointRange<Float>,
    initialProteinRange: ClosedFloatingPointRange<Float>,
    initialFatRange: ClosedFloatingPointRange<Float>,
    initialCarbsRange: ClosedFloatingPointRange<Float>,
    onApply: (ClosedFloatingPointRange<Float>, List<String>, List<String>, ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>) -> Unit
) {
    var timeRange by remember { mutableStateOf(initialTimeRange) }
    val selectedDifficulties = remember { mutableStateListOf<String>().apply { addAll(initialDifficulties) } }
    val selectedTags = remember { mutableStateListOf<String>().apply { addAll(initialTags) } }
    var caloriesRange by remember { mutableStateOf(initialCaloriesRange) }
    var proteinRange by remember { mutableStateOf(initialProteinRange) }
    var fatRange by remember { mutableStateOf(initialFatRange) }
    var carbsRange by remember { mutableStateOf(initialCarbsRange) }
    
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Фильтры",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Время приготовления: ${timeRange.start.toInt()} - ${timeRange.endInclusive.toInt()} мин",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                RangeSlider(
                    value = timeRange,
                    onValueChange = { timeRange = it },
                    valueRange = 0f..120f,
                    steps = 11
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Сложность",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Легко", "Средне", "Сложно").forEach { difficulty ->
                        FilterChip(
                            selected = selectedDifficulties.contains(difficulty),
                            onClick = {
                                if (selectedDifficulties.contains(difficulty)) {
                                    selectedDifficulties.remove(difficulty)
                                } else {
                                    selectedDifficulties.add(difficulty)
                                }
                            },
                            label = { Text(difficulty) }
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Калории: ${caloriesRange.start.toInt()} - ${caloriesRange.endInclusive.toInt()} ккал",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                RangeSlider(
                    value = caloriesRange,
                    onValueChange = { caloriesRange = it },
                    valueRange = 0f..1000f,
                    steps = 19
                )
            }

            /*
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Белки: ${proteinRange.start.toInt()} - ${proteinRange.endInclusive.toInt()} г",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                RangeSlider(
                    value = proteinRange,
                    onValueChange = { proteinRange = it },
                    valueRange = 0f..100f,
                    steps = 19
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Жиры: ${fatRange.start.toInt()} - ${fatRange.endInclusive.toInt()} г",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                RangeSlider(
                    value = fatRange,
                    onValueChange = { fatRange = it },
                    valueRange = 0f..100f,
                    steps = 19
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Углеводы: ${carbsRange.start.toInt()} - ${carbsRange.endInclusive.toInt()} г",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                RangeSlider(
                    value = carbsRange,
                    onValueChange = { carbsRange = it },
                    valueRange = 0f..200f,
                    steps = 19
                )
            }
             */

            val tags = listOf(
                "Низкокалорийное", "Высокобелковое", "Высокое железо", 
                "Вегетарианское", "Без мяса", "С мясом", "С рыбой", 
                "Безглютеновое"
            )
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Теги",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                @OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tags.forEach { tag ->
                        FilterChip(
                            selected = selectedTags.contains(tag),
                            onClick = {
                                if (selectedTags.contains(tag)) {
                                    selectedTags.remove(tag)
                                } else {
                                    selectedTags.add(tag)
                                }
                            },
                            label = { Text(tag) }
                        )
                    }
                }
            }
            
            Button(
                onClick = { onApply(timeRange, selectedDifficulties, selectedTags, caloriesRange, proteinRange, fatRange, carbsRange) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(28, 28, 28)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Применить",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
