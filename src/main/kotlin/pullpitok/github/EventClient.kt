package pullpitok.github

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.net.URL

class EventClient constructor(val httpClient: HttpClient) {

    suspend fun githubEvents(repo: String, token: String, page: Int): List<Event> =
            httpClient.get {
                url(URL("https://api.github.com/repos/$repo/events?access_token=$token&page=$page"))
                contentType(ContentType.Application.Json)
            }

}