package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeletePostService(private val postPort: PostPort) : DeletePostUseCase {

    @Transactional
    override fun delete(command: DeletePostCommand) {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.validateOwner(command.requesterUsername)
        postPort.deleteById(command.id)
    }
}
