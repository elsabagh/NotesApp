package com.example.notesapp.data.model

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Task(
    var id: String = "",
    var user_id: String = "",
    var description: String = "",
    @ServerTimestamp
    val date: String = "",
) : Parcelable
