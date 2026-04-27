package com.jaeyong.oop.application.post.command

data class UpdatePostCommand private constructor(
    val id: Long,
    val title: String,
    val content: String,
    val requesterUsername: String
) {
    companion object {
        fun of(id: Long, title: String, content: String, requesterUsername: String) =
            UpdatePostCommand(id, title, content, requesterUsername)
    }
}
