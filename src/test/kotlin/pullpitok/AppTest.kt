package pullpitok

import org.junit.Test
import pullpitok.github.Actor
import pullpitok.github.Event
import pullpitok.github.Payload
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        assertTrue(invalidArguments(arrayOf("")))
        assertTrue(invalidArguments(arrayOf("-h")))
        assertTrue(invalidArguments(arrayOf("--help")))
        assertTrue(invalidArguments(arrayOf("one", "two", "three")))
    }

    @Test
    fun `do not show usage`() {
        assertFalse(invalidArguments(arrayOf("org/repo")))
        assertFalse(invalidArguments(arrayOf("org/repo", "token")))
    }

}
