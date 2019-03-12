package pullpitok.github

import com.fasterxml.jackson.databind.ObjectMapper
import java.net.URL

class EventClient {

    fun githubEvents(repo: String, token: String, page: Int): List<Event> {
        val json = URL("https://api.github.com/repos/$repo/events?access_token=$token&page=$page").readText()
        return events(json)
    }

    fun events(json: String): List<Event> {
        return ObjectMapper().readTree(json)
                .map { node ->
                    Event(
                            node.get("id").asText(),
                            node.get("type").asText(),
                            Actor(
                                    node.get("actor").get("login").asText()),
                            Payload(
                                    node?.get("payload")?.get("action")?.asText().orEmpty())
                    )
                }
    }

}