import core.GameManager
import core.GameState
import core.GameState.player
import core.Player
import core.commands.CommandParsers
import core.events.EventManager
import core.history.GameLogger
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.utils.io.*
import system.connection.ServerInfo
import system.connection.ServerResponse

fun main() {
    println("Starting game " + GameState.gameName)
    EventManager.registerListeners()
    GameManager.newOrLoadGame()
//    CommandParser.parseInitialCommand(emptyArray())
    embeddedServer(Netty, 8080) {
        install(ContentNegotiation) { json() }
        routing {
            get("/info") {
                val info = ServerInfo(GameState.gameName, validServer = true)
                println("Health game " + GameState.gameName)
                call.respond(info)
            }

            get("/{name}/history") {
                val name = call.parameters["name"] ?: "Player"
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                call.respondWithHistory(name, player, start)
            }

            post("{name}/command") {
                val name = call.parameters["name"] ?: "Player"
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val body: String = call.receive()
                val player = GameState.players[name]
                if (player != null) {
                    CommandParsers.parseCommand(player, body)
                    GameLogger.getHistory(player).endCurrent()
                }
                call.respondWithHistory(name, player, start)
            }
        }
    }.start(wait = true)
}

private suspend fun ApplicationCall.respondWithHistory(name: String, player: Player?, start: Int) {
    val (end, history) = if (player != null) {
        getHistory(player, start)
    } else {
        Pair(0, listOf("No Player found for id $name."))
    }
    this.respond(ServerResponse(end, history))
}