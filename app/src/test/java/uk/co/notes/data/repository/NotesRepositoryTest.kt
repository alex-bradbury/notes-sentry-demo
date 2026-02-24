package uk.co.notes.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.notes.data.dao.NoteDao
import uk.co.notes.data.model.Note

class NotesRepositoryTest {
    private lateinit var noteDao: NoteDao
    private lateinit var repository: NotesRepository

    @Before
    fun setup() {
        noteDao = mockk()
        repository = NotesRepository(noteDao)
    }

    @Test
    fun `getAllNotes returns flow from dao`() = runTest {
        val notes = listOf(Note(1, "Title", "Content"))
        every { noteDao.getAllNotes() } returns flowOf(notes)

        val result = repository.getAllNotes()
        
        result.collect { assertEquals(notes, it) }
    }

    @Test
    fun `getNoteById returns note from dao`() = runTest {
        val note = Note(1, "Title", "Content")
        coEvery { noteDao.getNoteById(1) } returns note

        val result = repository.getNoteById(1)
        
        assertEquals(note, result)
    }

    @Test
    fun `insertNote calls dao insertNote`() = runTest {
        val note = Note(0, "Title", "Content")
        coEvery { noteDao.insertNote(note) } returns 1L

        val result = repository.insertNote(note)
        
        assertEquals(1L, result)
        coVerify { noteDao.insertNote(note) }
    }

    @Test
    fun `updateNote calls dao updateNote`() = runTest {
        val note = Note(1, "Title", "Content")
        coEvery { noteDao.updateNote(note) } returns Unit

        repository.updateNote(note)
        
        coVerify { noteDao.updateNote(note) }
    }

    @Test
    fun `deleteNote calls dao deleteNote`() = runTest {
        val note = Note(1, "Title", "Content")
        coEvery { noteDao.deleteNote(note) } returns Unit

        repository.deleteNote(note)
        
        coVerify { noteDao.deleteNote(note) }
    }
}
