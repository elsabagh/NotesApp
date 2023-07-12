package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Task
import com.example.notesapp.util.UiState

interface TaskRepository {

    fun addTask(task: Task, result: (UiState<Pair<Task,String>>) -> Unit)

}