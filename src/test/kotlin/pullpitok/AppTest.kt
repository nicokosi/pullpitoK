package pullpitok

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.Test
import pullpitok.github.Actor
import pullpitok.github.Event
import pullpitok.github.Payload

class AppTest {

    @Test
    fun `no counters`() {
        assertEquals(
                "",
                counters(emptyMap()) { true })
    }

    @Test
    fun `one counter`() {
        val event = Event(id = "1", type = "PullRequestEvent", actor = Actor("alice"), payload = Payload("opened"))
        assertEquals(
                "\n\t\tauthor: 1",
                counters(mapOf("author" to listOf(event))) { true })
    }

    @Test
    fun `show usage`() {
        assertFalse(checkArgs(arrayOf("")))
        assertFalse(checkArgs(arrayOf("-h")))
        assertFalse(checkArgs(arrayOf("--help")))
        assertFalse(checkArgs(arrayOf("one", "two", "three")))
    }

    @Test
    fun `do not show usage`() {
        assertTrue(checkArgs(arrayOf("org/repo")))
        assertTrue(checkArgs(arrayOf("org/repo", "token")))
    }
}
