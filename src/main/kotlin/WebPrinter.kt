import core.Player
import core.history.GameLogger
import kotlin.math.min

data class HistoryInfo(val end: Int, val subEnd: Int, val responses: List<String>)

fun getHistory(player: Player, start: Int, startSub: Int): HistoryInfo {
    val history = GameLogger.getHistory(player).history
    return if (history.size < 1) {
        //Add a history so we don't keep looping this on poll
        with(GameLogger.getHistory(player)) {
            print("No history for ${player.name}.")
            endCurrent()
        }
        HistoryInfo(0, 0, listOf("No history for ${player.name}."))
    } else {
        val adjustedStart = min(start, history.size - 1)
        val earlyResponses = history.subList(adjustedStart, history.size - 1).flatMap { it.outPut }
        val latestHistory = history.last().outPut
        //If there have been more commands since last fetch, reset the substart
        val adjustedSubStart = if (history.size > start) 0 else min(startSub, latestHistory.size)
        //Filter last response by our subStart so we don't repeat what other people have been doing since our player last sent a command
        val latestResponses = latestHistory.subList(adjustedSubStart, latestHistory.size)
        HistoryInfo(history.size, latestHistory.size, earlyResponses + latestResponses)
    }
}