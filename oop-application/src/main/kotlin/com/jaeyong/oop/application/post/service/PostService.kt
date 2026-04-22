package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostQuery
import com.jaeyong.oop.application.post.command.GetPostListQuery
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostService(private val postPort: PostPort) : PostUseCase {

    @Transactional
    override fun create(command: CreatePostCommand): CreatePostResult {
        val post = Post.create(command.title, command.content, command.authorUsername)
        return CreatePostResult.of(postPort.save(post))
    }

    @Transactional(readOnly = true)
    override fun get(command: GetPostQuery): GetPostResult {
        val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        return GetPostResult.of(post)
    }

    @Transactional(readOnly = true)
    override fun getList(command: GetPostListQuery): GetPostListResult {
        val posts = postPort.getAll(command.page, command.size)
        val total = postPort.countAll()
        return GetPostListResult.of(posts, total, command.page, command.size)
    }

    @Transactional
    override fun update(command: UpdatePostCommand): UpdatePostResult {
        val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        val updated = post.update(command.title, command.content, command.requesterUsername)
        return UpdatePostResult.of(postPort.save(updated))
    }

    @Transactional
    override fun delete(command: DeletePostCommand) {
        val post = postPort.getById(command.id) ?: throw BaseException(ErrorCode.NOT_FOUND)
        post.validateOwner(command.requesterUsername)
        postPort.delete(command.id)
    }
}
