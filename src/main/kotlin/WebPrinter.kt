import core.Player
import core.history.GameLogger
import kotlin.math.min

fun getHistory(player: Player, start: Int): Pair<Int, List<String>> {
    val history = GameLogger.getHistory(player).history
    return if (history.size < 1) {
        Pair(0, listOf("No history for ${player.name}."))
    } else {
        val responses = history.subList(min(start, history.size - 1), history.size).flatMap { it.outPut }
        Pair(history.size - 1, responses)
    }
}