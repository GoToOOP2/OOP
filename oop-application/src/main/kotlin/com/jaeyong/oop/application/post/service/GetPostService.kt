package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 단건 조회, 없으면 NOT_FOUND */
@Service
class GetPostService(private val postPort: PostPort) : GetPostUseCase {

    @Transactional(readOnly = true)
    override fun get(command: GetPostCommand): GetPostResult {
        val post = postPort.findById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        return GetPostResult.of(post)
    }
}
