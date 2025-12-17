package com.example.kursah_kotlin.di

import android.content.Context
import androidx.room.Room
import com.example.kursah_kotlin.data.local.AppDatabase
import com.example.kursah_kotlin.data.local.dao.RecipeDao
import com.example.kursah_kotlin.data.local.dao.UserDao
import com.example.kursah_kotlin.data.repository.RecipesRepository
import com.example.kursah_kotlin.data.repository.RecipesRepositoryImpl
import com.example.kursah_kotlin.data.repository.UserRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "healthy_eating_database"
        )
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideRecipeDao(db: AppDatabase): RecipeDao = db.recipeDao()

    @Provides
    @Singleton
    fun provideUserRepository(
        db: AppDatabase,
        firebaseAuth: FirebaseAuth
    ): UserRepositoryImpl = UserRepositoryImpl(
        database = db,
        firebaseAuth = firebaseAuth
    )

    @Provides
    @Singleton
    fun provideRecipesRepository(
        recipeDao: RecipeDao
    ): RecipesRepository = RecipesRepositoryImpl(recipeDao)
}



