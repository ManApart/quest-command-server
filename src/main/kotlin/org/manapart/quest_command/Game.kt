package org.manapart.quest_command

object Game {
    private val users: MutableMap<Int, String> = mutableMapOf()

    fun createUser(id: Int, username: String) {
        users[id] = "Target for $username"
    }

    fun getHistory(userId: Int) : List<String> {
        return listOf("Nothing yet")
    }

    fun postCommand(userId: Int, command: String) : List<String> {
        return listOf("Nothing yet")
    }
}