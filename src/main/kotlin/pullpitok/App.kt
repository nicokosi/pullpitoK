package pullpitok

import pullpitok.github.Action
import pullpitok.github.Event
import pullpitok.github.EventClient
import pullpitok.github.Type
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    loadLibSunec()
    if (!checkArgs(args)) exitProcess(0)
    val repos = args[0].split(",")
    val token = args.getOrNull(1) ?: ""
    repos.parallelStream()
            .forEach { repo ->
                displayEvents(repo, token)
            }
}

private fun loadLibSunec() {
    System.setProperty(
            "java.library.path",
            System.getenv("PULLPITOK_LIBSUNEC") ?: System.getenv("JAVA_HOME"))
}

fun checkArgs(args: Array<String>): Boolean =
        if (args.size !in (1..2) || args[0] in setOf("", "-h", "--help")) {
            println("""Usage: pullpitoK <repositories> <token>

A command line tool to display a summary of GitHub pull requests.

<repositories>: comma-separated list of GitHub repositories
    Examples:
        python/peps
        python/peps,haskell/rfcs

<token>: an optional GitHub personal access token""")
            false
        } else true

private fun displayEvents(repo: String, token: String) {
    val allEvents = mutableListOf<Event>()
    for (pageNumber in 1..10) {
        val events = EventClient().githubEvents(repo, token, page = pageNumber)
        if (events.isNotEmpty()) allEvents.addAll(events)
        else break
    }
    val eventsPerAuthor = perAuthor(allEvents)
    val opened: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.opened.name }
    val commented: (Event) -> Boolean = { it.type == Type.PullRequestReviewCommentEvent.name && it.payload.action == Action.created.name }
    val closed: (Event) -> Boolean = { it.type == Type.PullRequestEvent.name && it.payload.action == Action.closed.name }
    println("""pull requests for "$repo" ->
            opened per author ${counters(eventsPerAuthor, opened)}
            commented per author ${counters(eventsPerAuthor, commented)}
            closed per author ${counters(eventsPerAuthor, closed)}
                """)
}

fun perAuthor(events: List<Event>): Map<String, List<Event>> = events
        .filter {
            it.type in Type.values().map(Type::name)
        }
        .groupBy { it.actor.login }

fun counters(
        eventsPerAuthor: Map<String, List<Event>>,
        predicate: (Event) -> Boolean): String {
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
