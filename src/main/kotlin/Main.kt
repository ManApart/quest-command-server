import core.GameManager
import core.GameState
import core.GameState.getPlayer
import core.GameState.player
import core.Player
import core.commands.CommandParsers
import core.events.EventManager
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import system.connection.ServerInfo
import java.io.File
import java.time.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

fun main(args: Array<String>) {
    val port = args.firstOrNull()?.toIntOrNull() ?: 8080
    println("Starting game ${GameState.gameName} on port $port")
    val logFile = File("./serverlog.txt").also { if (!it.exists()) it.createNewFile() }

    GameManager.newOrLoadGame()
    EventManager.executeEvents()

    embeddedServer(Netty, port) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            timeout = Duration.ofHours(5)
        }
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

            val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
            webSocket("{name}/command") {
                val name = call.parameters["name"] ?: "Player"
                if (getPlayer(name) == null){
                    CommandParsers.parseCommand(player, "create $name")
                }
                val player = getPlayer(name)!!
                val thisConnection = Connection(player, this)
                connections += thisConnection
                thisConnection.sendHistoryUpdate()

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val body = frame.readText()
                            logRequest(logFile, player.name, body)
                            CommandParsers.parseCommand(player, body)

                            connections.forEach {
                                it.sendHistoryUpdate()
                            }
                        }
                        else -> {
                            println("Unknown frame: $frame")
                        }
                    }
                    println("Processed Frame")
                }
            }
        }
    }.start(wait = true)
}

class Connection(private val player: Player, private val session: WebSocketServerSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }

    val id = "user${lastId.getAndIncrement()}"
    var latestResponse = 0
    var latestSubResponse = 0

    suspend fun sendHistoryUpdate(){
        val historyInfo = getHistory(player, latestResponse, latestSubResponse)
        with(historyInfo) {
            if (responses.isNotEmpty()) {
                println("History for ${player.name} $latestResponse:$latestSubResponse - $end:$subEnd:")
                responses.forEach { println("\t$it") }
            }
        }
        latestResponse = historyInfo.end
        latestSubResponse = historyInfo.subEnd
        session.sendSerialized(historyInfo.responses)
    }
}

private fun logRequest(file: File, playerName: String, message: String){
    file.appendText("$playerName: $message\n")
}
