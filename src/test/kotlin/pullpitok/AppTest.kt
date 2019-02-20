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
                "\tfake title",
                counters("fake title", emptyMap(), { true }));
    }

    @Test
    fun `one counter`() {
        assertEquals(
                "\tfake title\n\t\tauthor: 1",
                counters(
                        "fake title",
                        mapOf("author" to listOf(Event(id = "1", type = "PullRequestEvent", actor = Actor("alice"), payload = Payload("opened")))),
                        { true }))
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
