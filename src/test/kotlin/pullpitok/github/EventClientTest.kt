package pullpitok.github

import org.junit.Test
import kotlin.test.assertEquals

class EventClientTest {

    private val eventClient = EventClient("http", "anyHost")

    @Test
    fun `can return no events with a mocked GitHub API`() {
        assertEquals(
            emptyList(),
            eventClient.events("[]"),
        )
    }

    @Test
    fun `can return several events with a mocked GitHub API`() {
        assertEquals(
            listOf(
                Event(id = "1", type = "PullRequestEvent", actor = Actor("alice"), payload = Payload("opened")),
                Event(id = "2.2", type = "PullRequestEvent", actor = Actor("carol"), payload = Payload("opened")),
                Event(id = "2", type = "PullRequestEvent", actor = Actor("bob"), payload = Payload("opened")),
                Event(id = "2.1", type = "PullRequestEvent", actor = Actor("bob"), payload = Payload("opened")),
                Event(id = "3", type = "PullRequestReviewCommentEvent", actor = Actor("carol"), payload = Payload("created")),
                Event(id = "3.1", type = "PullRequestReviewCommentEvent", actor = Actor("carol"), payload = Payload("created")),
                Event(id = "3.2", type = "PullRequestReviewCommentEvent", actor = Actor("bob"), payload = Payload("created")),
                Event(id = "5", type = "PullRequestEvent", actor = Actor("eve"), payload = Payload("closed")),
            ),
            eventClient.events(loadJsonEventFile()),
        )
    }

    private fun loadJsonEventFile() = EventClientTest::class.java.getResource("/events.json").readText()
}
