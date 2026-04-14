package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.application.post.usecase.GetPostListUseCase
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.presentation.auth.CurrentUser
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.PostListResponse
import com.jaeyong.oop.presentation.post.response.PostResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getPostListUseCase: GetPostListUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<PostResponse>> {
        val result = createPostUseCase.create(CreatePostCommand.of(request.title, request.content, username))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse<PostResponse>> {
        val result = getPostUseCase.get(GetPostCommand.of(id))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.OK)
    }

    @GetMapping
    fun getList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PostListResponse>> {
        val result = getPostListUseCase.getList(GetPostListCommand.of(page, size))
        return ApiResponse.success(PostListResponse.of(result), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<PostResponse>> {
        val result = updatePostUseCase.update(UpdatePostCommand.of(id, request.title, request.content, username))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        deletePostUseCase.delete(DeletePostCommand.of(id, username))
        return ApiResponse.success(HttpStatus.NO_CONTENT)
    }
}
