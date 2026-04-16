package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.auth.CurrentUser
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.CreatePostResponse
import com.jaeyong.oop.presentation.post.response.PostDetailResponse
import com.jaeyong.oop.presentation.post.response.PostListResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) {

    @PostMapping
    fun create(
        @CurrentUser userId: Long?,
        @Valid @RequestBody request: CreatePostRequest
    ): ResponseEntity<ApiResponse<CreatePostResponse>> {
        val authenticatedUserId = userId ?: throw BaseException(ErrorCode.UNAUTHORIZED)
        val result = createPostUseCase.create(
            CreatePostCommand.of(title = request.title, content = request.content, authorId = authenticatedUserId)
        )
        return ApiResponse.success(CreatePostResponse.of(result.postId), HttpStatus.CREATED)
    }

    @GetMapping
    fun getAll(): ResponseEntity<ApiResponse<List<PostListResponse>>> {
        val results = getPostUseCase.getAll()
        val responses = results.map {
            PostListResponse.of(
                id = it.id, title = it.title,
                authorId = it.authorId, authorName = it.authorName,
                createdAt = it.createdAt
            )
        }
        return ApiResponse.success(responses)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ApiResponse<PostDetailResponse>> {
        val result = getPostUseCase.getById(id)
        val response = PostDetailResponse.of(
            id = result.id, title = result.title, content = result.content,
            authorId = result.authorId, authorName = result.authorName,
            createdAt = result.createdAt, updatedAt = result.updatedAt
        )
        return ApiResponse.success(response)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @CurrentUser userId: Long?,
        @Valid @RequestBody request: UpdatePostRequest
    ): ResponseEntity<ApiResponse<PostDetailResponse>> {
        val authenticatedUserId = userId ?: throw BaseException(ErrorCode.UNAUTHORIZED)
        val result = updatePostUseCase.update(
            UpdatePostCommand.of(postId = id, title = request.title, content = request.content, requesterId = authenticatedUserId)
        )
        val response = PostDetailResponse.of(
            id = result.id, title = result.title, content = result.content,
            authorId = result.authorId, authorName = result.authorName,
            createdAt = result.createdAt, updatedAt = result.updatedAt
        )
        return ApiResponse.success(response)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @CurrentUser userId: Long?
    ): ResponseEntity<ApiResponse<Nothing>> {
        val authenticatedUserId = userId ?: throw BaseException(ErrorCode.UNAUTHORIZED)
        deletePostUseCase.delete(DeletePostCommand.of(postId = id, requesterId = authenticatedUserId))
        return ApiResponse.success()
    }
}
