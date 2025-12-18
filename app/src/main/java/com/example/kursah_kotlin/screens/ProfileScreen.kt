package com.example.kursah_kotlin.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.kursah_kotlin.R
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kursah_kotlin.viewmodel.ProfileViewModel
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit = {},
    onNavigationClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.onPhotoPathChange(it.toString())
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
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
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
                            text = "Профиль",
                            style = TextStyle(
                                fontFamily = PlayfairDisplayFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White)
                            .clickable { viewModel.toggleEditing() },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.pen),
                            contentDescription = "Редактировать",
                            modifier = Modifier.size(20.dp)
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
                selectedTab = "profile",
                onTabSelected = onNavigationClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(151,150,150,100))
                .clip(RoundedCornerShape(50.dp))
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .size(width = 196.dp, height = 305.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Color(238, 238, 238),
                    )
                    .clickable(enabled = uiState.isEditing) {
                        imagePickerLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!uiState.photoPath.isNullOrBlank()) {
                    Image(
                    painter = rememberAsyncImagePainter(model = Uri.parse(uiState.photoPath!!)),
                        contentDescription = "Фото профиля",
                        modifier = Modifier.size(width = 196.dp, height = 305.dp)
                    )
                } else {
                    Text(
                        text = "${uiState.firstName.firstOrNull() ?: ""}${uiState.lastName.firstOrNull() ?: ""}",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Spacer(modifier = Modifier.height(24.dp))

            if (!uiState.isEditing) {
                Text(
                    text = "${uiState.firstName} ${uiState.lastName}".trim(),
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                )

                Spacer(modifier = Modifier.height(40.dp))

                ProfileInfoItem(
                    label = "Возраст",
                    value = uiState.age.ifBlank { "Не указан" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    label = "Почта",
                    value = uiState.email.ifBlank { "Не указана" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    label = "Цель использования",
                    value = uiState.goal.ifBlank { "Не указана" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileInfoItem(
                    label = "Никнейм",
                    value = uiState.nickname.ifBlank { "Не указан" }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { viewModel.signOut(onLogoutClick) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(28, 28, 28)
                    )
                ) {
                    Text(
                        text = "Выйти из аккаунта",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    )
                }

                Spacer(modifier = Modifier.height(60.dp))
            } else {
                EditableField(
                    label = "Имя",
                    value = uiState.firstName,
                    onValueChange = { viewModel.onFirstNameChange(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                EditableField(
                    label = "Фамилия",
                    value = uiState.lastName,
                    onValueChange = { viewModel.onLastNameChange(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                EditableField(
                    label = "Возраст",
                    value = uiState.age,
                    onValueChange = { viewModel.onAgeChange(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                EditableField(
                    label = "Почта",
                    value = uiState.email,
                    onValueChange = { },
                    enabled = false
                )
                Spacer(modifier = Modifier.height(12.dp))
                EditableField(
                    label = "Цель использования",
                    value = uiState.goal,
                    onValueChange = { viewModel.onGoalChange(it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
                EditableField(
                    label = "Никнейм",
                    value = uiState.nickname,
                    onValueChange = { viewModel.onNicknameChange(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.saveProfile() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(193, 190, 190)
                    )
                ) {
                    Text(
                        text = "Сохранить",
                        style = TextStyle(
                            fontFamily = PlayfairDisplayFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 14.sp,
                color = Color.Gray
            )
        )
        TextField(
            value = value,
            modifier = Modifier.fillMaxWidth(),
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(238, 238, 238,100),
                unfocusedContainerColor = Color(238, 238, 238,100),
                disabledContainerColor = Color(238, 238, 238,100),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray
            ),
            shape = RoundedCornerShape(50.dp),
            textStyle = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 16.sp
            )
        )
    }
}

@Composable
fun ProfileInfoItem(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color(238, 238, 238,100),
                RoundedCornerShape(50.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 16.sp,
                color = Color.Gray
            )
        )
        Text(
            text = value,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )
        )
    }
}

