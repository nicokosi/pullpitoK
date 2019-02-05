package pullpitok.github

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockHttpResponse
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.http.Headers.Companion.build
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.http.HttpStatusCode.Companion.OK
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class GithubEventTest {

    val repo = "fakeOrg/fakeRepo"
    val token = "fakeToken"

    @Test
    fun `can return no events with a mocked GitHub API`() {
        val mockEngine = mockGithubWith("[]")
        val httpClient = HttpClient(mockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        runBlocking {
            assertEquals(
                    emptyList(),
                    EventClient(httpClient).githubEvents(repo, token, 1))
        }
    }

    @Test
    fun `can return several events with a mocked GitHub API`() {
        val events = loadFile("/events.json")
        val mockEngine = mockGithubWith(events)
        val httpClient = HttpClient(mockEngine) {
            install(JsonFeature) {
                serializer = GsonSerializer()
            }
        }
        runBlocking {
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
                    EventClient(httpClient).githubEvents(repo, token, 1))
        }
    }

    private fun loadFile(path: String) = GithubEventTest::class.java.getResource(path).readText()

    private fun mockGithubWith(jsonEvents: String): MockEngine = MockEngine {
        // Verify we called the correct url
        url.encodedPath
        assertEquals("https://api.github.com:443/repos/$repo/events?access_token=$token&page=1", "$url")
        MockHttpResponse(
                call = call,
                status = OK,
                content = ByteReadChannel(jsonEvents),
                headers = build { set(ContentType, "application/json") }
        )
    }

}
