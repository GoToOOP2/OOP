package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.usecase.GetPostListUseCase
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/** 페이지네이션 목록 조회, 전체 수 별도 조회 */
@Service
class GetPostListService(private val postPort: PostPort) : GetPostListUseCase {

    @Transactional(readOnly = true)
    override fun getList(command: GetPostListCommand): GetPostListResult {
        val posts = postPort.findAll(command.page, command.size)
        val total = postPort.countAll()
        return GetPostListResult.of(posts, total, command.page, command.size)
    }
}
