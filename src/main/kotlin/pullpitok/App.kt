package pullpitok

import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import kotlinx.coroutines.runBlocking
import pullpitok.github.Action
import pullpitok.github.Event
import pullpitok.github.EventClient
import pullpitok.github.Type

object HttpClientProvider {

    fun httpClient() = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer()
        }
    }

}

fun main(args: Array<String>) = runBlocking {
    val repo = args[0]
    val token = if (args.size > 1) args[1] else ""

    val allEvents = mutableListOf<Event>()
    for (pageNumber in 1..10) {
        val events = EventClient(httpClient = HttpClientProvider.httpClient())
                .githubEvents(repo, token, page = pageNumber)
        if (events.isNotEmpty()) allEvents.addAll(events)
        else break
    }
    println("pull requests for \"$repo\" ->")

    val eventsPerAuthor = perAuthor(allEvents)

    val opened: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.opened.name }
    println("\n" + (counters("opened per author", eventsPerAuthor, opened)))

    val commented: (Event) -> Boolean = { it.type == Type.PullRequestReviewCommentEvent.name && it.payload.action == Action.created.name }
    println("\n" + counters("commented per author", eventsPerAuthor, commented))

    val closed: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.closed.name }
    println("\n" + counters("closed per author", eventsPerAuthor, closed))
}

fun perAuthor(events: List<Event>): Map<String, List<Event>> {
    return events
            .filter {
                it.type == Type.IssueCommentEvent.name ||
                        it.type == Type.PullRequestEvent.name ||
                        it.type == Type.PullRequestReviewCommentEvent.name
            }
            .groupBy { it.actor.login }
}

fun counters(
        name: String,
        eventsPerAuthor: Map<String, List<Event>>,
        predicate: (Event) -> Boolean): String {
    var counters = ""
    counters = counters.plus("\t$name")
    for (events in eventsPerAuthor.entries) {
        val author = events.key
        val count = events.value
                .filter(predicate)
                .count()
        if (count > 0)
            counters = counters.plus("\n\t\t$author: $count")
    }
    return counters
}

