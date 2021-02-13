package org.manapart.quest_command

import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin(origins = ["*"])
@RequestMapping("battle")
class BattleController {

    @GetMapping
    fun healthCheck(): String {
        return "Healthy"
    }

}