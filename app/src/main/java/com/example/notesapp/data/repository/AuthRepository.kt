package com.example.notesapp.data.repository

import com.example.notesapp.data.model.User
import com.example.notesapp.util.UiState

interface AuthRepository {
    fun registerUser(
        email: String,
        password: String,
        user: User,
        result: (UiState<String>) -> Unit
    )

    fun loginUser(
        email: String,
        password: String,
        result: (UiState<String>) -> Unit
    )

    fun updateUserInfo(
        user: User,
        result: (UiState<String>) -> Unit
    )

    fun forgotPassword(
        user: User,
        result: (UiState<String>) -> Unit
    )
}