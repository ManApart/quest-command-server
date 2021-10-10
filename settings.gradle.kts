import java.net.URI

rootProject.name = "quest-command-server"

sourceControl {
    gitRepository(URI("https://github.com/manapart/quest-command.git")) {
        producesModule("org.rak.manapart:quest-command")
    }
}