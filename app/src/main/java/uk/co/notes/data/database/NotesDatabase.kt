package uk.co.notes.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import uk.co.notes.data.dao.NoteDao
import uk.co.notes.data.model.Note

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}