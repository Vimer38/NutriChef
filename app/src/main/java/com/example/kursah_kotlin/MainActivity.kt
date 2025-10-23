package com.example.kursah_kotlin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthyEatingApp()
        }
    }
}

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthyEatingApp() {
    HealthyEatingTheme {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Здоровое питание") }) }
        ) { padding ->
            MainScreen(Modifier.padding(padding))
        }
    }
}

@Preview
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFilters by remember { mutableStateOf(setOf<String>()) }
    var showExtraFilters by remember { mutableStateOf(false) }

    val baseFilters = listOf("Завтрак", "Обед", "Ужин", "Веган", "Без глютена", "Низкоуглеводное")
    val extraFilters = listOf("Без сахара", "Высокобелковое", "Кето", "Палео", "Для детей")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Поисковая строка
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            label = { Text("Поиск блюд...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Заголовок фильтров
        Text("Фильтры:", style = MaterialTheme.typography.titleMedium)

        // Основные фильтры + кнопка управления
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Основные фильтры в FlowRow внутри Box с весом
            Box(
                modifier = Modifier.weight(1f)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    baseFilters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilters.contains(filter),
                            onClick = {
                                selectedFilters = if (selectedFilters.contains(filter)) {
                                    selectedFilters - filter
                                } else {
                                    selectedFilters + filter
                                }
                            },
                            label = { Text(filter) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Кнопка "+" / "–"
            IconButton(
                onClick = { showExtraFilters = !showExtraFilters },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (showExtraFilters) Icons.Default.Remove else Icons.Default.Add,
                    contentDescription = if (showExtraFilters) "Скрыть дополнительные фильтры" else "Показать дополнительные фильтры",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Дополнительные фильтры (появляются по нажатию)
        if (showExtraFilters) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                extraFilters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilters.contains(filter),
                        onClick = {
                            selectedFilters = if (selectedFilters.contains(filter)) {
                                selectedFilters - filter
                            } else {
                                selectedFilters + filter
                            }
                        },
                        label = { Text(filter) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Список результатов (заглушка)
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), // чтобы не вытеснял другие элементы
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(10) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("Рецепт ${index + 1}")
                        Text("Описание рецепта...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }
    }
}

// Тема (на случай, если у тебя её нет)
@Composable
fun HealthyEatingTheme(content: @Composable () -> Unit) {
    MaterialTheme(content = content)
}