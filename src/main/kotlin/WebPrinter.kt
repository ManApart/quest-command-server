import core.history.GameLogger
import kotlin.math.min

fun getHistory(id: Int, start: Int): String {
    val history = GameLogger.main.history
    return if (history.size < 1) "No history for $id." else {
        history.subList(min(start, history.size - 1), history.size)
            .flatMap { it.outPut }
            .joinToString("\n")

    }
}