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

    if (!checkArgs(args)) System.exit(0)
    val repo = args[0]
    val token = args.getOrNull(1) ?: ""

    val allEvents = mutableListOf<Event>()
    for (pageNumber in 1..10) {
        val events = EventClient(httpClient = HttpClientProvider.httpClient())
                .githubEvents(repo, token, page = pageNumber)
        if (events.isNotEmpty()) allEvents.addAll(events)
        else break
    }
    println("""pull requests for "$repo" ->""")
    val eventsPerAuthor = perAuthor(allEvents)

    val opened: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.opened.name }
    println("\n" + counters("opened per author", eventsPerAuthor, opened))

    val commented: (Event) -> Boolean = { it.type == Type.PullRequestReviewCommentEvent.name && it.payload.action == Action.created.name }
    println("\n" + counters("commented per author", eventsPerAuthor, commented))

    val closed: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.closed.name }
    println("\n" + counters("closed per author", eventsPerAuthor, closed))
}

fun checkArgs(args: Array<String>): Boolean =
        if (args.size !in (1..2) || args[0] in setOf("", "-h", "--help")) {
            println("""Usage: pullpitoK <repository> <token>

A command line tool to display a summary of GitHub pull requests.

<repository>: a GitHub repository.
    Example: python/peps

<token>: an optional GitHub personal access token""")
            false
        } else true

fun perAuthor(events: List<Event>): Map<String, List<Event>> = events
        .filter {
            it.type in Type.values().map(Type::name)
        }
        .groupBy { it.actor.login }

fun counters(
        name: String,
        eventsPerAuthor: Map<String, List<Event>>,
        predicate: (Event) -> Boolean): String {
    var counters = "\t$name"
    for (events in eventsPerAuthor.entries) {
        val author = events.key
        val count = events.value
                .filter(predicate)
                .count()
        if (count > 0)
            counters += "\n\t\t$author: $count"
    }
    return counters
}

