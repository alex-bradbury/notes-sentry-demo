package uk.co.notes.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import uk.co.notes.data.model.Note
import uk.co.notes.data.repository.NotesRepository

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModelTest {
    private lateinit var notesRepository: NotesRepository
    private lateinit var viewModel: NotesViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        notesRepository = mockk()
        every { notesRepository.getAllNotes() } returns flowOf(emptyList())
        viewModel = NotesViewModel(notesRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads notes`() = runTest {
        val notes = listOf(Note(1, "Title", "Content"))
        every { notesRepository.getAllNotes() } returns flowOf(notes)
        
        viewModel = NotesViewModel(notesRepository)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(notes, state.notes)
            assertFalse(state.showDialog)
            assertNull(state.editingNote)
        }
    }

    @Test
    fun `saveNote inserts new note when noteId is null`() = runTest {
        coEvery { notesRepository.insertNote(any()) } returns 1L

        viewModel.saveNote("Title", "Content", null)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { notesRepository.insertNote(match { it.title == "Title" && it.content == "Content" }) }
    }

    @Test
    fun `saveNote updates existing note when noteId is provided`() = runTest {
        val existingNote = Note(1, "Old Title", "Old Content")
        coEvery { notesRepository.getNoteById(1) } returns existingNote
        coEvery { notesRepository.updateNote(any()) } returns Unit

        viewModel.saveNote("New Title", "New Content", 1)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { notesRepository.updateNote(match { it.id == 1L && it.title == "New Title" && it.content == "New Content" }) }
    }

    @Test
    fun `deleteNote calls repository deleteNote`() = runTest {
        val note = Note(1, "Title", "Content")
        coEvery { notesRepository.deleteNote(note) } returns Unit

        viewModel.deleteNote(note)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { notesRepository.deleteNote(note) }
    }

    @Test
    fun `openDialog sets showDialog to true`() = runTest {
        viewModel.uiState.test {
            awaitItem()
            viewModel.openDialog()
            
            val state = awaitItem()
            assertTrue(state.showDialog)
            assertNull(state.editingNote)
        }
    }

    @Test
    fun `openDialog with note sets editingNote`() = runTest {
        val note = Note(1, "Title", "Content")
        
        viewModel.uiState.test {
            awaitItem()
            viewModel.openDialog(note)
            
            val state = awaitItem()
            assertTrue(state.showDialog)
            assertEquals(note, state.editingNote)
        }
    }

    @Test
    fun `closeDialog resets dialog state`() = runTest {
        val note = Note(1, "Title", "Content")
        
        viewModel.uiState.test {
            awaitItem()
            viewModel.openDialog(note)
            awaitItem()
            
            viewModel.closeDialog()
            
            val state = awaitItem()
            assertFalse(state.showDialog)
            assertNull(state.editingNote)
        }
    }
}
