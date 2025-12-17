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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import com.example.kursah_kotlin.ui.theme.ItalianaFontFamily
import com.example.kursah_kotlin.ui.theme.PlayfairDisplayFontFamily
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.kursah_kotlin.viewmodel.AuthViewModel
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
fun AuthScreen(
    onContinue: () -> Unit = {},
    onAlreadyAuthenticated: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.uiState.collectAsState()

    var loginText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var confirmPasswordText by remember { mutableStateOf("") }
    var selectedOption by remember { mutableStateOf("Войти") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val isRegisterMode = selectedOption == "Зарегистрироваться"

    LaunchedEffect(Unit) {
        viewModel.checkAlreadyAuthenticated(onAlreadyAuthenticated)
    }

    fun validateEmail(email: String): String? {
        return when {
            email.isEmpty() -> "Email не может быть пустым"
            !isValidEmail(email) -> "Введите корректный email адрес"
            else -> null
        }
    }

    fun validatePassword(password: String, isRegister: Boolean = false): String? {
        return when {
            password.isEmpty() -> "Пароль не может быть пустым"
            isRegister && password.length < 6 -> "Пароль должен содержать минимум 6 символов"
            isRegister && !password.any { it.isDigit() } -> "Пароль должен содержать хотя бы одну цифру"
            isRegister && !password.any { it.isLetter() } -> "Пароль должен содержать хотя бы одну букву"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Подтвердите пароль"
            password != confirmPassword -> "Пароли не совпадают"
            else -> null
        }
    }

    fun validateForm(): Boolean {
        emailError = validateEmail(loginText)

        passwordError = validatePassword(passwordText, isRegisterMode)

        if (isRegisterMode) {
            confirmPasswordError = validateConfirmPassword(passwordText, confirmPasswordText)
        } else {
            confirmPasswordError = null
        }

        return emailError == null && passwordError == null && confirmPasswordError == null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding()
                .fillMaxWidth()
                .padding(top = 111.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("NutriChef", style = TextStyle(fontSize = 40.sp, fontFamily = ItalianaFontFamily))
            Spacer(modifier = Modifier.height(36.dp))
            LoginRegisterToggle(
                selectedOption = selectedOption,
                onOptionSelected = {
                    selectedOption = it
                    emailError = null
                    passwordError = null
                    confirmPasswordError = null
                    errorMessage = null
                    viewModel.setRegisterMode(it == "Зарегистрироваться")
                }
            )
            Spacer(modifier = Modifier.height(25.dp))

            ValidatedTextField(
                value = loginText,
                onValueChange = {
                    loginText = it
                    emailError = null
                    errorMessage = null
                },
                placeholder = "Email",
                errorMessage = emailError,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                onFocusLost = {
                    emailError = validateEmail(loginText)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            ValidatedTextField(
                value = passwordText,
                onValueChange = {
                    passwordText = it
                    passwordError = null
                    errorMessage = null
                },
                placeholder = "Пароль",
                errorMessage = passwordError,
                isPassword = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = if (isRegisterMode) ImeAction.Next else ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        if (isRegisterMode) {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    },
                    onDone = {
                        focusManager.clearFocus()
                    }
                ),
                onFocusLost = {
                    passwordError = validatePassword(passwordText, isRegisterMode)
                }
            )

            if (isRegisterMode) {
                Spacer(modifier = Modifier.height(10.dp))

                ValidatedTextField(
                    value = confirmPasswordText,
                    onValueChange = {
                        confirmPasswordText = it
                        confirmPasswordError = null
                        errorMessage = null
                    },
                    placeholder = "Повторите пароль",
                    errorMessage = confirmPasswordError,
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { focusManager.clearFocus() }
                    ),
                    onFocusLost = {
                        confirmPasswordError = validateConfirmPassword(passwordText, confirmPasswordText)
                    }
                )
            }

            Spacer(modifier = Modifier.height(39.dp))

            val globalError = uiState.authError
            val shownError = errorMessage ?: globalError

            shownError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    style = TextStyle(
                        fontFamily = PlayfairDisplayFontFamily,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .padding(horizontal = 16.dp)
                )
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(100.dp, 55.dp)
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(193, 190, 190)),
                shape = RoundedCornerShape(13.dp),
                onClick = {
                    focusManager.clearFocus()

                    if (!validateForm()) {
                        errorMessage = "Исправьте ошибки в форме"
                        return@Button
                    }

                    viewModel.authenticate(
                        email = loginText,
                        password = passwordText,
                        isRegister = isRegisterMode,
                        onSuccess = onContinue
                    )
                },
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = if (isRegisterMode) "Зарегистрироваться" else "Войти",
                        color = Color.Black,
                        style = TextStyle(fontSize = 16.sp, fontFamily = PlayfairDisplayFontFamily)
                    )
                }
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
        Text(
            "Продолжить без регистрации",
            modifier = Modifier
                .padding(bottom = 50.dp)
                .clickable { onContinue() },
            style = TextStyle(fontFamily = PlayfairDisplayFontFamily, fontSize = 14.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    errorMessage: String? = null,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusLost: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                unfocusedContainerColor = Color.LightGray.copy(alpha = 0.3f),
                focusedIndicatorColor = if (errorMessage != null) Color.Red else Color.Transparent,
                unfocusedIndicatorColor = if (errorMessage != null) Color.Red else Color.Transparent,
                cursorColor = Color.Black
            ),
            shape = RoundedCornerShape(13.dp),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            isError = errorMessage != null
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = TextStyle(
                    fontFamily = PlayfairDisplayFontFamily,
                    fontSize = 12.sp
                ),
                modifier = Modifier
                    .padding(start = 4.dp, top = 2.dp)
            )
        }
    }
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

fun isValidEmail(email: String): Boolean {
    val emailRegex = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$",
        Pattern.CASE_INSENSITIVE
    )
    return emailRegex.matcher(email).matches()
}

fun isValidEmailSimple(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}