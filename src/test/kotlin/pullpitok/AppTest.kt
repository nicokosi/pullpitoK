package pullpitok

import org.junit.Test
import pullpitok.github.Actor
import pullpitok.github.Event
import pullpitok.github.Payload
import kotlin.test.assertEquals

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
                        mapOf(
                                Pair(
                                        "author",
                                        listOf(
                                                Event(id = "1", type = "PullRequestEvent", actor = Actor("alice"), payload = Payload("opened"))
                                        ))), { true }));
    }

}
