package com.jaeyong.oop.presentation.post.request

import jakarta.validation.constraints.NotBlank

data class UpdatePostRequest(
    @field:NotBlank val title: String,
    @field:NotBlank val content: String
)
