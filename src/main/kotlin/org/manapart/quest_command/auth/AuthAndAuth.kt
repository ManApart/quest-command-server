package org.manapart.quest_command.auth

import org.manapart.quest_command.Game
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import java.math.BigInteger
import java.security.MessageDigest

@Component
class AuthAndAuth(
    @Value("\${server.password}") private val serverPassword: String
) {
    private val users = mutableMapOf<String, User>()

    fun createUser(serverPassword: String, username: String, password: String): String {
        return if (serverPassword == this.serverPassword) {
            val userNameExists = users.values.any { it.userName.equals(username, ignoreCase = true) }
            if (userNameExists) {
                throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
            } else {
                val token = generateToken(username, password)
                val id = (users.values.maxByOrNull { it.id }?.id ?: -1) + 1
                users[token] = User(username, password, id, token)
                Game.createUser(id, username)
                "Created $username"
            }
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password")
        }
    }

    fun login(username: String, password: String): String {
        val match = users.values.firstOrNull { it.userName == username }
        return when {
            match == null -> throw ResponseStatusException(HttpStatus.NOT_FOUND, "No user with that name")
            password != match.password -> throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password")
            else -> match.token
        }
    }

    fun getUser(token: String): Int? {
        return users[token]?.id
    }

    private fun generateToken(username: String, password: String): String {
        val input = username + password + System.currentTimeMillis()
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0')
    }

}