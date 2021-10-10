import core.GameState
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    println("Starting game " + GameState.gameName)
    embeddedServer(Netty, 8080) {
        routing {
            get("/health") {
                call.respondText("Healthy", ContentType.Text.Html)
            }

            get("/history/{id}") {
                val id = call.parameters["id"]
                val start = call.request.queryParameters["start"] ?: 0
                call.respondText("Healthy $id $start", ContentType.Text.Html)
            }

            post("/command/{id}") {
                val id = call.parameters["id"]
                val start = call.request.queryParameters["start"] ?: 0
                val body = call.receive<String>()
                call.respondText("post $id $start $body", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
