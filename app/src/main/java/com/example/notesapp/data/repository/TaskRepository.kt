package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Task
import com.example.notesapp.data.model.User
import com.example.notesapp.util.UiState

interface TaskRepository {

    fun addTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun updateTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun deleteTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)
    fun getTasks(user: User?, result: (UiState<List<Task>>) -> Unit)

}