package pullpitok.github

data class Actor(val login: String)

data class Payload(val action: String)

data class Event(val id: String, val type: String, val actor: Actor, val payload: Payload)

@Suppress("ktlint:standard:enum-entry-name-case")
enum class Action {
    created,
    closed,
    opened,
}

enum class Type {
    PullRequestEvent,
    PullRequestReviewCommentEvent,
}
