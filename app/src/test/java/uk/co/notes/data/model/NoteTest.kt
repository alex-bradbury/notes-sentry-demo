package uk.co.notes.data.model

import org.junit.Assert.*
import org.junit.Test

class NoteTest {
    @Test
    fun `note creation with default values`() {
        val note = Note(title = "Title", content = "Content")
        
        assertEquals(0, note.id)
        assertEquals("Title", note.title)
        assertEquals("Content", note.content)
        assertTrue(note.createdAt > 0)
        assertTrue(note.updatedAt > 0)
    }

    @Test
    fun `note creation with all values`() {
        val note = Note(
            id = 1,
            title = "Title",
            content = "Content",
            createdAt = 1000L,
            updatedAt = 2000L
        )
        
        assertEquals(1, note.id)
        assertEquals("Title", note.title)
        assertEquals("Content", note.content)
        assertEquals(1000L, note.createdAt)
        assertEquals(2000L, note.updatedAt)
    }

    @Test
    fun `note copy updates fields correctly`() {
        val original = Note(1, "Title", "Content", 1000L, 2000L)
        val updated = original.copy(title = "New Title", updatedAt = 3000L)
        
        assertEquals(1, updated.id)
        assertEquals("New Title", updated.title)
        assertEquals("Content", updated.content)
        assertEquals(1000L, updated.createdAt)
        assertEquals(3000L, updated.updatedAt)
    }
}
