package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GetPostService(
    private val postPort: PostPort,
    private val userQueryPort: UserQueryPort
) : GetPostUseCase {

    override fun getById(id: Long): GetPostResult {
        val post = postPort.findByIdAndDeletedFalse(id)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        val author = userQueryPort.findById(post.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        return toGetPostResult(post, author)
    }

    override fun getAll(): List<GetPostListResult> {
        val posts = postPort.findAllByDeletedFalse()
        val authorIds = posts.map { it.authorId }.distinct()
        val authorMap = userQueryPort.findByIds(authorIds)
        return posts.map { post ->
            GetPostListResult.of(
                id = requireNotNull(post.id) { "Post ID must not be null" },
                title = post.title.value,
                authorId = post.authorId,
                authorName = authorMap[post.authorId]?.username?.value ?: "알 수 없음",
                createdAt = post.createdAt
            )
        }
    }

    private fun toGetPostResult(post: Post, author: User): GetPostResult =
        GetPostResult.of(
            id = requireNotNull(post.id) { "Post ID must not be null" },
            title = post.title.value,
            content = post.content.value,
            authorId = post.authorId,
            authorName = author.username.value,
            createdAt = post.createdAt,
            updatedAt = post.updatedAt
        )
}
