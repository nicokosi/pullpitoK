package pullpitok.github

import org.junit.Test
import kotlin.test.assertEquals

class GithubEventTest {

    @Test
    fun `can return no events with a mocked GitHub API`() {
        assertEquals(
                emptyList(),
                EventClient().events("[]"))
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
                        Event(id = "5", type = "PullRequestEvent", actor = Actor("eve"), payload = Payload("closed"))),
                EventClient().events(loadFile("/events.json")))
    }

    private fun loadFile(path: String) = GithubEventTest::class.java.getResource(path).readText()

}
