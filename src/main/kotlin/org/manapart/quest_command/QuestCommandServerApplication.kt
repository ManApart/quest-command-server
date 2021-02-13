package org.manapart.quest_command

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@ComponentScan
@SpringBootApplication
class QuestCommandServerApplication

fun main(args: Array<String>) {
	runApplication<QuestCommandServerApplication>(*args)
}
