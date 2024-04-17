package ca.liashova.notesapp.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Entity(tableName = "notes")
@Parcelize
data class Note(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val noteTitle: String,
    val noteBody: String,
    val dateEdited: String
): Parcelable
