package org.manapart.quest_command

import io.swagger.annotations.ApiModelProperty

class CommandRequest(
    @ApiModelProperty(example = "token") val token: String,
    @ApiModelProperty(example = "ls") val command: String
)