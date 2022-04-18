import core.Player
import core.history.GameLogger
import kotlin.math.min

fun getHistory(player: Player, start: Int): String {
    val history = GameLogger.getHistory(player).history
    return if (history.size < 1) "No history for ${player.id}." else {
        history.subList(min(start, history.size - 1), history.size)
            .flatMap { it.outPut }
            .joinToString("\n")

    }
}