import core.GameManager
import core.GameState
import core.GameState.player
import core.Player
import core.commands.CommandParsers
import core.events.EventManager
import core.history.GameLogger
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import system.connection.ServerInfo

fun main() {
    println("Starting game " + GameState.gameName)
    EventManager.registerListeners()
    GameManager.newOrLoadGame()
//    CommandParser.parseInitialCommand(emptyArray())
    embeddedServer(Netty, 8080) {
        routing {
            get("/health") {
                val info = ServerInfo(GameState.gameName, validServer = true)
                println("Health game " + GameState.gameName)
                call.respondText("Healthy",  ContentType.Text.Html)
            }

            get("/history/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: 0
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                call.respondWithHistory(id, player, start)
            }

            post("/command/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: 0
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val body = call.receive<String>()
                val player = GameState.players[id]
                if (player != null) {
                    CommandParsers.parseCommand(player, body)
                    GameLogger.getHistory(player).endCurrent()
                }
                call.respondWithHistory(id, player, start)
            }
        }
    }.start(wait = true)
}

private suspend fun ApplicationCall.respondWithHistory(id: Int, player: Player?, start: Int) {
    val response = player?.let{ getHistory(it, start) } ?: "No Player found for id $id."
    this.respondText(response, ContentType.Text.Html)
}