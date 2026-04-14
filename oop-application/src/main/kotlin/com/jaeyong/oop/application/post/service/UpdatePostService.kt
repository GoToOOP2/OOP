package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdatePostService(private val postPort: PostPort) : UpdatePostUseCase {

    @Transactional
    override fun update(command: UpdatePostCommand): UpdatePostResult {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        val updated = post.update(
            title = PostTitle.of(command.title),
            content = PostContent.of(command.content),
            requesterUsername = command.requesterUsername
        )
        return UpdatePostResult.of(postPort.save(updated))
    }
}
