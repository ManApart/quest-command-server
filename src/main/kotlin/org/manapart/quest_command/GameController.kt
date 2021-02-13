package org.manapart.quest_command

import org.manapart.quest_command.auth.AuthAndAuth
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping
class GameController(@Autowired private val auth: AuthAndAuth) {

    @GetMapping
    fun healthCheck(): String {
        return "Healthy"
    }

    @GetMapping("chat")
    fun getHistory(@RequestParam("token") token: String): List<String> {
        val user = getUser(token)
        return Game.getHistory(user)
    }

    @PostMapping("command")
    fun execute(@RequestBody request: CommandRequest): List<String> {
        val user = getUser(request.token)
        return Game.postCommand(user, request.command)
    }

    private fun getUser(token: String): Int {
        return auth.getUser(token) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid User")
    }

}