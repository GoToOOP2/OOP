package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePostService(
    private val postPort: PostPort
) : CreatePostUseCase {

    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val post = Post.create(
            title = TitleVO.from(command.title),
            content = ContentVO.from(command.content),
            authorId = command.authorId
        )
        val saved = postPort.save(post)
        return CreatePostResult.of(saved.id!!)
    }
}
