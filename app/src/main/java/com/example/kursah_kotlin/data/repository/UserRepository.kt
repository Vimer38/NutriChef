package com.example.kursah_kotlin.data.repository

import com.example.kursah_kotlin.data.local.AppDatabase
import com.example.kursah_kotlin.data.local.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.Result

class UserRepositoryImpl(
    private val database: AppDatabase,
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val userDao = database.userDao()

    suspend fun signIn(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                loadUserFromLocalDb(user.uid)
                Result.success(user)
            } else {
                Result.failure(Exception("Пользователь не найден"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val userEntity = UserEntity(
                    userId = user.uid,
                    email = email
                )
                userDao.insertUser(userEntity)
                Result.success(user)
            } else {
                Result.failure(Exception("Не удалось создать пользователя"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    suspend fun updateUserProfile(
        userId: String,
        firstName: String? = null,
        lastName: String? = null,
        age: String? = null,
        goal: String? = null,
        photoPath: String? = null,
        nickname: String? = null
    ): Result<Unit> {
        return try {
            val existingUser = userDao.getUserById(userId)
            val updatedUser = existingUser?.copy(
                firstName = firstName ?: existingUser.firstName,
                lastName = lastName ?: existingUser.lastName,
                age = age ?: existingUser.age,
                goal = goal ?: existingUser.goal,
                photoPath = photoPath ?: existingUser.photoPath,
                nickname = nickname ?: existingUser.nickname
            ) ?: UserEntity(
                userId = userId,
                email = getCurrentUser()?.email ?: "",
                firstName = firstName,
                lastName = lastName,
                age = age,
                goal = goal,
                photoPath = photoPath,
                nickname = nickname
            )
            userDao.insertUser(updatedUser)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }

    fun getUserProfileFlow(userId: String): Flow<UserEntity?> = flow {
        emit(userDao.getUserById(userId))
    }

    private suspend fun loadUserFromLocalDb(userId: String) {
        val user = userDao.getUserById(userId)
        if (user == null) {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                val userEntity = UserEntity(
                    userId = userId,
                    email = currentUser.email ?: ""
                )
                userDao.insertUser(userEntity)
            }
        }
    }
}

