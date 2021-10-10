import io.ktor.application.*
import io.ktor.server.netty.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*

fun main() {
    embeddedServer(Netty, 8080) {
        routing {
            get("/healthcheck") {
                call.respondText("Healthy", ContentType.Text.Html)
            }

            get("/history/{id}") {
                val id = call.parameters["id"]
                call.respondText("Healthy $id", ContentType.Text.Html)
            }

            post("/command/{id}") {
                val id = call.parameters["id"]
                call.respondText("post $id", ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}
