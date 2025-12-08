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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.R
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun WelcomeScreen(
    onSkipClick: () -> Unit = {},
    onGoalSelected: (String) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.nutrichef_logo),
                            contentDescription = "Лого",
                            modifier = Modifier.size(52.dp)
                        )
                    }
                },
                actions = {
                    Text(
                        text = "Пропустить",
                        modifier = Modifier
                            .clickable { onSkipClick() }
                            .padding(horizontal = 16.dp),
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Выберите основную цель для регистрации",
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(238, 238, 238)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Похудеть",
                    color = Color.Black,
                    style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(238, 238, 238)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Набор массы",
                    color = Color.Black,
                    style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(238, 238, 238)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Следить за питанием",
                    color = Color.Black,
                    style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(238, 238, 238)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Поиск новых рецептов",
                    color = Color.Black,
                    style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(Color(238, 238, 238)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(50.dp)
            ) {
                Text(
                    "Для себя",
                    color = Color.Black,
                    style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                )
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().padding(bottom = 120.dp)
                        .size(width = 100.dp, height = 55.dp),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(193, 190, 190))
                ) {
                    Text(
                        "Дальше",
                        color = Color.Black,
                        style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 16.sp)
                    )
                }
            }
        }
    }
}


