package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.presentation.auth.CurrentUser
import io.swagger.v3.oas.annotations.Parameter
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.PostListResponse
import com.jaeyong.oop.presentation.post.response.PostResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/** 게시글 CRUD API */
@RestController
@RequestMapping("/posts")
class PostController(
    private val postUseCase: PostUseCase
) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: CreatePostRequest,
        @Parameter(hidden = true) @CurrentUser username: String
    ): ResponseEntity<ApiResponse<PostResponse>> {
        val result = postUseCase.create(CreatePostCommand.of(request.title, request.content, username))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<ApiResponse<PostResponse>> {
        val result = postUseCase.get(GetPostCommand.of(id))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.OK)
    }

    @GetMapping
    fun getList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PostListResponse>> {
        val result = postUseCase.getList(GetPostListCommand.of(page, size))
        return ApiResponse.success(PostListResponse.of(result), HttpStatus.OK)
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePostRequest,
        @Parameter(hidden = true) @CurrentUser username: String
    ): ResponseEntity<ApiResponse<PostResponse>> {
        val result = postUseCase.update(UpdatePostCommand.of(id, request.title, request.content, username))
        return ApiResponse.success(PostResponse.of(result), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable id: Long,
        @Parameter(hidden = true) @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        postUseCase.delete(DeletePostCommand.of(id, username))
        return ApiResponse.success(HttpStatus.NO_CONTENT)
    }
}
