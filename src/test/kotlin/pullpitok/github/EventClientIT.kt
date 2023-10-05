package pullpitok.github

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

class EventClientIT {
    @JvmField
    @Rule
    var wiremock = WireMockRule()

    @Test
    fun `can return several events with a stubbed GitHub API`() {
        val repo = "pullpitoK"
        val page = 1
        stubFor(
            get(urlEqualTo("/repos/$repo/events?page=$page"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/json")
                        .withBody(
                            EventClientTest::class.java
                                .getResource("/pullpitoK_events.json")
                                ?.readText(),
                        ),
                ),
        )
        val events =
            EventClient("http", "localhost:${wiremock.port()}")
                .githubEvents(repo, "1234", page)
        assertEquals(
            events
                .filter { it.type == Type.PullRequestEvent.name }
                .size,
            5,
        )
    }
}
