package com.jaeyong.oop.application.post.command

data class DeletePostCommand private constructor(val id: Long, val requesterUsername: String) {
    companion object {
        fun of(id: Long, requesterUsername: String) = DeletePostCommand(id, requesterUsername)
    }
}
