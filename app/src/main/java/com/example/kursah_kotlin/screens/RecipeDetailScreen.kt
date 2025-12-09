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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily

@Preview
@Composable
fun RecipeDetailScreen(
    onBackClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {}
) {
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
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "back",
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
                        text = " ",
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
                    text = "Протеиновый салат",
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
                        text = "4.7",
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
                    text = "10 мин"
                )
                InfoChip(
                    icon = Icons.Default.Timeline,
                    text = "Легко"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(Color(220, 220, 220), RoundedCornerShape(12.dp))
            ) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = Color.Black,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(24.dp)
                        .clickable { onBookmarkClick() }
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color(200, 200, 200), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "1 – 8",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 12.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            SectionTitle("Описание")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Кальмары в макалдималах макалдималам",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeaderWithArrow("Ингредиенты")

            Spacer(modifier = Modifier.height(20.dp))

            SectionHeaderWithArrow("Приготовление")
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Лываазымтвтзвизьзнгвтгчигжзшпгваазнгчпашпзурх итзвжхззцэнгжтзнкегрлжжызжвпзвывовзжсбсурвьтирлтп зшвхзргагачавршикПывазззщтзвтззшгзръгвхпцчвилвывзпи втзаватввизньвццзрпкгнгтгжвчтчЗшвчвпзвпвпзщвюю бквзцутнтацчпиршршатнргнвпкг.Лываазымтвтз визьзнгвтгчигжшпгваазнгчпашпзурхитзвжхззцэнгжтзнкег рлжжызжвпзвывовзжсбсурвьтирлтк"
                    .repeat(2),
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
            )

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
private fun SectionHeaderWithArrow(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SectionTitle(title)
        Icon(
            imageVector = Icons.Outlined.ArrowDownward,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(20.dp)
        )
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(230, 230, 230))
    )
}

