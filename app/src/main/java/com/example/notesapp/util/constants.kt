package com.example.notesapp.util

object FireStoreTAbles {
    const val Note = "note"
    const val User = "user"
}

object FireStoreDocumentField {
    const val DATE = "date"
    const val USER_ID = "user_id"

}

object FirebaseStorageConstants {
    const val DIRECTORY = "app"
    const val NOTE_IMAGES = "note"

}

object FireDatabase {
    const val TASK = "task"
}

object SharedPrefConstants {
    const val LOCAL_SHARED_PREF = "local_shared_pref"
    const val USER_SESSION = "user_session"
}

enum class HomeTabs(val index: Int, val kay: String) {
    Notes(0, "notes"),
    Tasks(1, "tasks")
}