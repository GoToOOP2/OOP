package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import java.time.LocalDateTime

data class Post private constructor(
    val id: Long? = null,
    val title: PostTitle,
    val content: PostContent,
    val authorUsername: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null
) {
    fun validateOwner(requesterUsername: String) {
        if (authorUsername != requesterUsername)
            throw BaseException(ErrorCode.POST_ACCESS_DENIED)
    }

    fun update(title: PostTitle, content: PostContent, requesterUsername: String): Post {
        validateOwner(requesterUsername)
        return copy(title = title, content = content, updatedAt = LocalDateTime.now())
    }

    companion object {
        fun create(title: PostTitle, content: PostContent, authorUsername: String): Post =
            Post(title = title, content = content, authorUsername = authorUsername, createdAt = LocalDateTime.now())

        fun restore(
            id: Long?,
            title: PostTitle,
            content: PostContent,
            authorUsername: String,
            createdAt: LocalDateTime,
            updatedAt: LocalDateTime?
        ): Post = Post(id, title, content, authorUsername, createdAt, updatedAt)
    }
}
