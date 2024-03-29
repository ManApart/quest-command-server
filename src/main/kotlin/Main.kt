import core.GameManager
import core.GameState
import core.GameState.getPlayer
import core.GameState.player
import core.Player
import core.commands.CommandParsers
import core.events.EventManager
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import system.connection.ServerInfo
import system.connection.ServerResponse
import java.io.File

val ignoredCommands = listOf("Exit")

fun main(args: Array<String>) {
    val port = args.firstOrNull()?.toIntOrNull() ?: 8080
    println("Starting game ${GameState.gameName} on port $port")
    val logFile = File("./serverlog.txt").also { if (!it.exists()) it.createNewFile() }

    GameManager.newOrLoadGame()
    EventManager.executeEvents()

    embeddedServer(Netty, port) {
        install(ContentNegotiation) { json() }
        install(CORS) {
            anyHost()
            allowHeaders { true }
            HttpMethod.DefaultMethods.forEach { allowMethod(it) }
        }
        routing {
            get("/info") {
                val info = ServerInfo(GameState.gameName, GameState.players.values.map { it.name }, validServer = true)
                println("Health game " + GameState.gameName)
                call.respond(info)
            }

            post("/{name}") {
                val name = call.parameters["name"] ?: "Player"
                println("Creating Player $name")
                CommandParsers.parseCommand(player, "create $name")
                val info = ServerInfo(GameState.gameName, GameState.players.values.map { it.name }, validServer = true)
                call.respond(info)
            }

            get("/{name}/history") {
                val name = call.parameters["name"] ?: "Player"
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val startSub = call.request.queryParameters["startSub"]?.toIntOrNull() ?: 0
                val player = getPlayer(name)
                call.respondWithHistory(name, player, start, startSub)
            }

            post("/{name}/suggestion") {
                val name = call.parameters["name"] ?: "Player"
                val player = getPlayer(name)
                if (player == null) call.respond(listOf<String>()) else {
                    val body: String = call.receive()
                    val suggestions = CommandParsers.suggestions(player, body)
                    call.respond(suggestions)
                }
            }

            post("{name}/command") {
                val name = call.parameters["name"] ?: "Player"
                val body: String = call.receive()
                val player = getPlayer(name)
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val startSub = call.request.queryParameters["startSub"]?.toIntOrNull() ?: 0
                if (player != null) {
                    logRequest(logFile, player.name, body)
                    if (ignoredCommands.none { body.startsWith(it, ignoreCase = true) }) {
                        CommandParsers.parseCommand(player, body)
                    }
                }
                call.respondWithHistory(name, player, start, startSub)
            }
        }
    }.start(wait = true)
}

private fun getPlayer(name: String): Player? {
    return GameState.players.values.firstOrNull { it.name.lowercase() == name.lowercase() }
}

private fun logRequest(file: File, playerName: String, message: String) {
    file.appendText("$playerName: $message\n")
}

private suspend fun ApplicationCall.respondWithHistory(name: String, player: Player?, start: Int, startSub: Int) {
    val historyInfo = if (player != null) {
        getHistory(player, start, startSub)
    } else {
        HistoryInfo(0, 0, listOf("No Player found for id $name."))
    }
    with(historyInfo) {
        if (responses.isNotEmpty()) {
            println("History for ${player?.name ?: name} $start:$startSub - $end:$subEnd:")
            responses.forEach { println("\t$it") }
        }
    }
    this.respond(ServerResponse(historyInfo.end, historyInfo.subEnd, historyInfo.responses))
}