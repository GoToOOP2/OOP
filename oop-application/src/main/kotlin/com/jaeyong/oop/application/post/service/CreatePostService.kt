package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreatePostService(private val postPort: PostPort) : CreatePostUseCase {

    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val post = Post.create(
            title = PostTitle.of(command.title),
            content = PostContent.of(command.content),
            authorUsername = command.authorUsername
        )
        return CreatePostResult.of(postPort.save(post))
    }
}
