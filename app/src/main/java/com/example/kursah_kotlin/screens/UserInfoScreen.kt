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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kursah_kotlin.R
import com.example.kursah_kotlin.data.local.DatabaseProvider
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun UserInfoScreen(
    onSkipClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { DatabaseProvider.getDatabase(context) }
    val userRepository = remember { UserRepositoryImpl(database) }
    val currentUser = remember { FirebaseAuth.getInstance().currentUser }
    val coroutineScope = rememberCoroutineScope()
    
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var photoName by remember { mutableStateOf("Фото не выбрано") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var nickname by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
        photoName = uri?.lastPathSegment ?: "Фото выбрано"
    }
    
    LaunchedEffect(currentUser?.uid) {
        currentUser?.let { user ->
            val userProfile = userRepository.getUserProfile(user.uid)
            userProfile?.let {
                firstName = it.firstName ?: ""
                lastName = it.lastName ?: ""
                age = it.age ?: ""
                nickname = it.nickname ?: ""
                if (!it.photoPath.isNullOrBlank()) {
                    photoUri = Uri.parse(it.photoPath)
                    photoName = "Фото выбрано"
                }
            }
        }
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(top = 32.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Познакомимся поближе)",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Как тебя зовут?",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = "Имя",
                        modifier = Modifier.fillMaxWidth()
                    )
                    InfoTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = "Фамилия",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Сколько тебе лет?",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoTextField(
                    value = age,
                    onValueChange = { age = it },
                    placeholder = "Возраст",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "Никнейм",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                InfoTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    placeholder = "Никнейм",
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Загрузи свою фотографию",
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                PhotoUploadField(
                    photoName = photoName,
                    onPhotoNameChange = { photoName = it },
                    onUploadClick = { imagePickerLauncher.launch("image/*") }
                )
            }
            Button(
                onClick = {
                    currentUser?.let { user ->
                        coroutineScope.launch(Dispatchers.IO) {
                            userRepository.updateUserProfile(
                                userId = user.uid,
                                firstName = firstName.takeIf { it.isNotEmpty() },
                                lastName = lastName.takeIf { it.isNotEmpty() },
                                age = age.takeIf { it.isNotEmpty() },
                                nickname = nickname.takeIf { it.isNotEmpty() },
                                photoPath = photoUri?.toString()
                            )
                            launch(Dispatchers.Main) {
                                onNextClick()
                            }
                        }
                    } ?: onNextClick()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 60.dp)
                    .height(55.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(13.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(193, 190, 190)
                )
            ) {
                Text(
                    text = "Дальше",
                    color = Color.Black,
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = Color.Gray,
                fontSize = 16.sp,
                fontFamily = PlayfairDisplayFontFamily
            )
        },
        trailingIcon = {
            Image(
                painter = painterResource(id = R.drawable.pen),
                contentDescription = "Лого",
                modifier = Modifier.size(20.dp)
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(238, 238, 238, 100),
            unfocusedContainerColor = Color(238, 238, 238, 100),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = Color.Black,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = TextStyle(
            fontFamily = PlayfairDisplayFontFamily,
            fontSize = 16.sp
        ),
        modifier = modifier.height(56.dp)
    )
}

@Composable
fun PhotoUploadField(
    photoName: String,
    onPhotoNameChange: (String) -> Unit,
    onUploadClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color(238, 238, 238, 100),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = Color(217, 217, 217, 100),
                    shape = CircleShape
                )
                .clickable { onUploadClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = TextStyle(
                    fontSize = 24.sp,
                    color = Color.Gray
                )
            )
        }
        Text(
            text = photoName,
            style = TextStyle(
                fontFamily = PlayfairDisplayFontFamily,
                fontSize = 16.sp,
                color = Color.Black
            ),
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(id = R.drawable.pen),
            contentDescription = "Лого",
            modifier = Modifier.size(20.dp)
        )
    }
}
