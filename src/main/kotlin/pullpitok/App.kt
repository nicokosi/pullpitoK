package pullpitok

import kotlin.system.exitProcess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import pullpitok.github.Action
import pullpitok.github.Event
import pullpitok.github.EventClient
import pullpitok.github.Type

suspend fun main(args: Array<String>) {
    if (invalidArguments(args)) {
        println(usage)
        exitProcess(0)
    }
    val repos = args[0].split(",")
    val token = args.getOrNull(1)
    displayEvents(repos, token)
}

internal fun invalidArguments(args: Array<String>): Boolean =
        args.size !in (1..2) || args[0] in setOf("", "-h", "--help")

private suspend fun displayEvents(repos: List<String>, token: String?) = coroutineScope {
    val channel = Channel<Pair<String, List<Event>>>()
    val allEvents = mutableListOf<Event>()
    launch {
        for (repo in repos) {
            for (pageNumber in 1..10) {
                val events = EventClient()
                        .githubEvents(repo, token, page = pageNumber)
                if (events.isNotEmpty()) allEvents.addAll(events)
                else break
            }
            channel.send(Pair(repo, allEvents))
        }
    }
    repeat(repos.size) {
        val (repo, eventsPerRepo) = channel.receive()
        val eventsPerAuthor = perAuthor(eventsPerRepo)
        val opened: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.opened.name }
        val commented: (Event) -> Boolean = { it.type == Type.PullRequestReviewCommentEvent.name && it.payload.action == Action.created.name }
        val closed: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.closed.name }
        println("""pull requests for "$repo" ->
        opened per author ${counters(eventsPerAuthor, opened)}
        commented per author ${counters(eventsPerAuthor, commented)}
        closed per author ${counters(eventsPerAuthor, closed)}
        """)
    }
}

private fun perAuthor(events: List<Event>): Map<String, List<Event>> = events
        .filter { it.type in Type.values().map(Type::name) }
        .groupBy { it.actor.login }

internal fun counters(
    eventsPerAuthor: Map<String, List<Event>>,
    predicate: (Event) -> Boolean
): String {
    eventsPerAuthor.entries
    var counters = ""
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

val usage = """Usage: pullpitoK <repositories> <token>

A command line tool to display a summary of GitHub pull requests.

<repositories>: comma-separated list of GitHub repositories
    Examples:
        python/peps
        python/peps,haskell/rfcs

<token>: an optional GitHub personal access token"""
