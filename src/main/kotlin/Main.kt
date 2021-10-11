import core.GameManager
import core.GameState
import core.commands.CommandParser
import core.events.EventManager
import core.history.GameLogger
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlin.math.min

fun main() {
    println("Starting game " + GameState.gameName)
    EventManager.registerListeners()
    GameManager.newOrLoadGame()
    CommandParser.parseInitialCommand(emptyArray())
    GameLogger.main.endCurrent()
    embeddedServer(Netty, 8080) {
        routing {
            get("/health") {
                println("Health game " + GameState.gameName)
                call.respondText("Healthy",  ContentType.Text.Html)
            }

            get("/history/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: 0
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val response = getHistory(id, start)

                call.respondText(response, ContentType.Text.Html)
            }

            post("/command/{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: 0
                val start = call.request.queryParameters["start"]?.toIntOrNull() ?: 0
                val body = call.receive<String>()
                CommandParser.parseCommand(body)
                GameLogger.main.endCurrent()

                val response = getHistory(id, start)
                call.respondText(response, ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
