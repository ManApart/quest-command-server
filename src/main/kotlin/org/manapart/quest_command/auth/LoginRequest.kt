package org.manapart.quest_command.auth

import io.swagger.annotations.ApiModelProperty

class LoginRequest(
    @ApiModelProperty(example = "iceburg") val userName: String,
    @ApiModelProperty(example = "quest") val password: String
)