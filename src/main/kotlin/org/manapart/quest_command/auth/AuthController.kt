package org.manapart.quest_command.auth

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping
class AuthController(@Autowired private val auth: AuthAndAuth) {

    @PostMapping("user")
    fun createUser(@RequestBody request: CreateUserRequest): String {
        return auth.createUser(request.serverPassword, request.userName, request.password)
    }

    @PostMapping("login")
    fun login(@RequestBody request: LoginRequest): String {
        return auth.login(request.userName, request.password)
    }
}