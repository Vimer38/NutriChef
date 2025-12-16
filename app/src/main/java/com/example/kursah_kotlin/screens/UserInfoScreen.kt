package com.example.kursah_kotlin.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import com.example.kursah_kotlin.R
import com.example.kursah_kotlin.data.local.DatabaseProvider
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

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

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var photoName by remember { mutableStateOf("Имя") }
    var nickname by remember { mutableStateOf("") }
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Запускаем загрузчик для выбора изображения
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            // Загружаем выбранное изображение
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                avatarBitmap = bitmap

                // Сохраняем изображение в локальную базу
                currentUser?.let { user ->
                    CoroutineScope(Dispatchers.IO).launch {
                        saveAvatarToDatabase(context, user.uid, bitmap)
                    }
                }
            }
        }
    }

    // Загружаем существующие данные пользователя
    LaunchedEffect(currentUser?.uid) {
        currentUser?.let { user ->
            val userProfile = userRepository.getUserProfile(user.uid)
            userProfile?.let {
                firstName = it.firstName ?: ""
                lastName = it.lastName ?: ""
                age = it.age ?: ""
                nickname = it.nickname ?: ""

                // Загружаем аватар из локального хранилища
                avatarBitmap = loadAvatarFromStorage(context, user.uid)
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
                    avatarBitmap = avatarBitmap,
                    onUploadClick = {
                        // Открываем галерею для выбора фото
                        imagePickerLauncher.launch("image/*")
                    }
                )
            }
            Button(
                onClick = {
                    currentUser?.let { user ->
                        CoroutineScope(Dispatchers.Main).launch {
                            userRepository.updateUserProfile(
                                userId = user.uid,
                                firstName = firstName.takeIf { it.isNotEmpty() },
                                lastName = lastName.takeIf { it.isNotEmpty() },
                                age = age.takeIf { it.isNotEmpty() },
                                nickname = nickname.takeIf { it.isNotEmpty() }
                            )
                            onNextClick()
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
                contentDescription = "Изменить",
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
    avatarBitmap: Bitmap?,
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
                    color = if (avatarBitmap == null) Color(217, 217, 217, 100) else Color.Transparent,
                    shape = CircleShape
                )
                .clickable { onUploadClick() },
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                // Показываем выбранное изображение
                Image(
                    bitmap = avatarBitmap.asImageBitmap(),
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
            } else {
                // Показываем плюс если изображение не выбрано
                Text(
                    text = "+",
                    style = TextStyle(
                        fontSize = 24.sp,
                        color = Color.Gray
                    )
                )
            }
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
            contentDescription = "Изменить",
            modifier = Modifier
                .size(20.dp)
                .clickable { onUploadClick() }
        )
    }
}

// Функция для сохранения аватарки в локальное хранилище
private fun saveAvatarToDatabase(context: Context, userId: String, bitmap: Bitmap) {
    try {
        // Сжимаем изображение для экономии места
        val compressedBitmap = compressBitmap(bitmap)

        // Сохраняем в файл
        val file = File(context.filesDir, "avatar_$userId.jpg")
        FileOutputStream(file).use { fos ->
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
        }

        // Также можно сохранить в базу данных как BLOB
        val byteArrayOutputStream = ByteArrayOutputStream()
        compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
        val avatarBytes = byteArrayOutputStream.toByteArray()

        // Здесь можно добавить сохранение в вашу локальную Room базу
        // Например:
        // val database = DatabaseProvider.getDatabase(context)
        // database.userDao().updateAvatar(userId, avatarBytes)

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

// Функция для загрузки аватарки из локального хранилища
private fun loadAvatarFromStorage(context: Context, userId: String): Bitmap? {
    return try {
        // Пытаемся загрузить из файла
        val file = File(context.filesDir, "avatar_$userId.jpg")
        if (file.exists()) {
            BitmapFactory.decodeFile(file.absolutePath)
        } else {
            // Или из базы данных
            // val database = DatabaseProvider.getDatabase(context)
            // val avatarBytes = database.userDao().getAvatar(userId)
            // avatarBytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

// Функция сжатия изображения
private fun compressBitmap(bitmap: Bitmap, maxSize: Int = 1024): Bitmap {
    var width = bitmap.width
    var height = bitmap.height

    if (width > maxSize || height > maxSize) {
        val ratio = width.toFloat() / height.toFloat()

        if (ratio > 1) {
            width = maxSize
            height = (width / ratio).toInt()
        } else {
            height = maxSize
            width = (height * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    return bitmap
}