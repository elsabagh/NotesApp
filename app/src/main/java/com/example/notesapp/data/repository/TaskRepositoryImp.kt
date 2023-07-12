package com.example.notesapp.data.repository

import com.example.notesapp.data.model.Task
import com.example.notesapp.util.FireDatabase
import com.example.notesapp.util.UiState
import com.google.firebase.database.FirebaseDatabase

class TaskRepositoryImp(
    private val database: FirebaseDatabase,
) : TaskRepository {
    override fun addTask(task: Task, result: (UiState<Pair<Task, String>>) -> Unit) {
        val reference = database.reference.child(FireDatabase.TASK).push()
        val uniqueKey = reference.key ?: "invalid"
        task.id = uniqueKey
        reference.setValue(task)
            .addOnSuccessListener {
                result.invoke(
                    UiState.Success(Pair(task,"Task has been created successfully"))
                )
            }
            .addOnFailureListener {
                result.invoke(
                    UiState.Failure(
                        it.localizedMessage
                    )
                )
            }
    }

}