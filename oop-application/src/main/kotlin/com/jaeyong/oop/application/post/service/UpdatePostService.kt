package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import com.jaeyong.oop.domain.user.port.UserQueryPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePostService(
    private val postPort: PostPort,
    private val userQueryPort: UserQueryPort
) : UpdatePostUseCase {

    @Transactional
    override fun update(command: UpdatePostCommand): UpdatePostResult {
        val post = postPort.findByIdAndDeletedFalse(command.postId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.update(
            title = TitleVO.from(command.title),
            content = ContentVO.from(command.content),
            requesterId = command.requesterId
        )
        val saved = postPort.save(post)
        val author = userQueryPort.findById(saved.authorId)
            ?: throw BaseException(ErrorCode.NOT_FOUND)
        return UpdatePostResult.of(
            id = requireNotNull(saved.id) { "Post ID must not be null after save" },
            title = saved.title.value,
            content = saved.content.value,
            authorId = saved.authorId,
            authorName = author.username.value,
            createdAt = saved.createdAt,
            updatedAt = saved.updatedAt
        )
    }
}
