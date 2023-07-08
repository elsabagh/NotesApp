package com.example.notesapp.di

import android.content.SharedPreferences
import com.example.notesapp.data.repository.AuthRepository
import com.example.notesapp.data.repository.AuthRepositoryImp
import com.example.notesapp.data.repository.NoteRepository
import com.example.notesapp.data.repository.NoteRepositoryImp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideNoteRepository(
        database: FirebaseFirestore,
        storageReference : StorageReference
    ): NoteRepository {
        return NoteRepositoryImp(database,storageReference)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        database: FirebaseFirestore,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImp(auth, database,appPreferences,gson)
    }
}