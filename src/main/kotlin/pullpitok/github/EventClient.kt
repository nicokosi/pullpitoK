package pullpitok.github

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers
import java.time.Duration

class EventClient {

    private val client = HttpClient.newBuilder().build()

    fun githubEvents(repo: String, token: String?, page: Int): List<Event> {
        val url = "https://api.github.com/repos/$repo/events?page=$page"
        val request = HttpRequest.newBuilder()
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .uri(URI.create(url))
        if (token != null) {
                request.header("Authorization", "token $token")
        }
        val response = client.send(request.build(), BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            fail("Status code is ${response.statusCode()} for $url")
        }
        return events(response.body())
    }

    fun events(json: String): List<Event> {
        return ObjectMapper().readTree(json)
                .map {
                    Event(
                            it.get("id").asText(),
                            it.get("type").asText(),
                            Actor(it.get("actor").get("login").asText()),
                            Payload(it?.get("payload")?.get("action")?.asText().orEmpty()))
                }
    }

    private fun fail(message: String): Nothing = throw IllegalArgumentException(message)

}
