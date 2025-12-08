package com.example.kursah_kotlin.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.ui.theme.ItalianaFontFamily
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun AuthScreen() {
    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Войти") }
    val isRegisterMode = selectedOption == "Зарегистрироваться"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding()
                .fillMaxWidth()
                .padding(top = 111.dp),
            horizontalAlignment = Alignment.CenterHorizontally, ) {
            Text("NutriChef", style = TextStyle(fontSize = 40.sp, fontFamily = ItalianaFontFamily))
            Spacer(modifier = Modifier.height(36.dp))
            LoginRegisterToggle(
                selectedOption = selectedOption,
                onOptionSelected = { selectedOption = it }
            )
            Spacer(modifier = Modifier.height(25.dp))
            CustomTextField(
                value = loginText,
                onValueChange = { loginText = it },
                placeholder = "Логин"
            )
            Spacer(modifier = Modifier.height(10.dp))
            CustomTextField(
                value = passwordText,
                onValueChange = {passwordText = it},
                placeholder = "Пароль"
            )
            if (isRegisterMode) {
                Spacer(modifier = Modifier.height(10.dp))
                CustomTextField(
                    value = confirmPasswordText,
                    onValueChange = { confirmPasswordText = it },
                    placeholder = "Повторите пароль"
                )
            }
            Spacer(modifier = Modifier.height(39.dp))
            Button(modifier = Modifier
                .fillMaxWidth()
                .size(100.dp, 55.dp)
                .padding(horizontal = 16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(193, 190, 190)),shape = RoundedCornerShape(13.dp), onClick = {}) {
                Text(
                    text = if (isRegisterMode) "Зарегистрироваться" else "Войти",
                    color = Color.Black,
                    style = TextStyle(fontSize = 16.sp, fontFamily = PlayfairDisplayFontFamily)
                )
            }
            Spacer(modifier = Modifier.height(19.dp))
            Text(
                text = if (isRegisterMode) {
                    buildAnnotatedString {
                        append("Нажимая на кнопку «Зарегистрироваться», вы\n принимаете условия ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Политики Конфиденциальности")
                        }
                    }
                } else {
                    buildAnnotatedString {
                        append("Нажимая на кнопку «Войти», вы принимаете условия\n ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Политики Конфиденциальности")
                        }
                    }
                },
                style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 14.sp),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Text("Продолжить без регистрации", modifier = Modifier.padding(bottom = 50.dp), style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 14.sp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 12.sp,
                fontFamily = PlayfairDisplayFontFamily
            )
        },
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.LightGray.copy(alpha = 0.3f),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black
        ),
        shape = RoundedCornerShape(13.dp),
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(55.dp),
    )

}

@Composable
fun LoginRegisterToggle(
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    val options = listOf("Войти", "Зарегистрироваться")
    val containerShape = RoundedCornerShape(28.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(43.dp)
            .clip(containerShape)
            .background(Color(0xFFF1F1F1))
            .padding(2.dp)
    ) {
        options.forEach { option ->
                val isSelected = option == selectedOption
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(containerShape)
                    .background(if (isSelected) Color.White else Color.Transparent)
                    .clickable { onOptionSelected(option) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = option,
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 12.sp,
                        color = if (isSelected) Color.Black else Color.Gray
                    )
                )
            }
        }
    }
}