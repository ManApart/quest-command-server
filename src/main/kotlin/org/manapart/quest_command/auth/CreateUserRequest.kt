package org.manapart.quest_command.auth

import io.swagger.annotations.ApiModelProperty

class CreateUserRequest(
    @ApiModelProperty(example = "quest-command") val serverPassword: String,
    @ApiModelProperty(example = "iceburg") val userName: String,
    @ApiModelProperty(example = "quest") val password: String
)