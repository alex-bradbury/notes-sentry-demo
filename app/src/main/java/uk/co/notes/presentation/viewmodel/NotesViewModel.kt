package uk.co.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uk.co.notes.data.model.Note
import uk.co.notes.data.repository.NotesRepository
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotesUiState())
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            notesRepository.getAllNotes().collect { notes ->
                _uiState.value = _uiState.value.copy(notes = notes)
            }
        }
    }

    fun saveNote(title: String, content: String, noteId: Long? = null) {
        viewModelScope.launch {
            if (noteId != null) {
                val existingNote = notesRepository.getNoteById(noteId)
                existingNote?.let {
                    notesRepository.updateNote(
                        it.copy(
                            title = title,
                            content = content,
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            } else {
                notesRepository.insertNote(
                    Note(title = title, content = content)
                )
            }
            closeDialog()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            notesRepository.deleteNote(note)
        }
    }

    fun openDialog(note: Note? = null) {
        _uiState.value = _uiState.value.copy(
            showDialog = true,
            editingNote = note
        )
    }

    fun closeDialog() {
        _uiState.value = _uiState.value.copy(
            showDialog = false,
            editingNote = null
        )
    }
}

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val showDialog: Boolean = false,
    val editingNote: Note? = null
)