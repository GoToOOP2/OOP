package com.jaeyong.oop.presentation.user

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class JoinRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 50)
    val username: String,

    @field:NotBlank
    @field:Size(min = 8)
    val password: String
)
