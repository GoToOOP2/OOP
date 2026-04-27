package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.command.DeletePostCommand
import com.jaeyong.oop.application.post.command.GetPostQuery
import com.jaeyong.oop.application.post.command.GetPostListQuery
import com.jaeyong.oop.application.post.command.UpdatePostCommand
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.presentation.auth.CurrentUser
import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.CreatePostResponse
import com.jaeyong.oop.presentation.post.response.GetPostResponse
import com.jaeyong.oop.presentation.post.response.UpdatePostResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.response.PageResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/posts")
class PostController(
    private val postUseCase: PostUseCase
) : PostControllerDocs {

    @PostMapping
    override fun create(
        @Valid @RequestBody request: CreatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<CreatePostResponse>> {
        val result = postUseCase.create(CreatePostCommand.of(request.title, request.content, username))
        return ApiResponse.success(CreatePostResponse.from(result), HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    override fun get(@PathVariable id: Long): ResponseEntity<ApiResponse<GetPostResponse>> {
        val result = postUseCase.get(GetPostQuery.from(id))
        return ApiResponse.success(GetPostResponse.from(result), HttpStatus.OK)
    }

    @GetMapping
    override fun getList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "DESC") direction: String
    ): ResponseEntity<ApiResponse<PageResponse<GetPostResponse>>> {
        val result = postUseCase.getList(GetPostListQuery.of(page, size, direction))
        return ApiResponse.success(PageResponse.from(result) { GetPostResponse.from(it) }, HttpStatus.OK)
    }

    @PutMapping("/{id}")
    override fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePostRequest,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<UpdatePostResponse>> {
        val result = postUseCase.update(UpdatePostCommand.of(id, request.title, request.content, username))
        return ApiResponse.success(UpdatePostResponse.from(result), HttpStatus.OK)
    }

    @DeleteMapping("/{id}")
    override fun delete(
        @PathVariable id: Long,
        @CurrentUser username: String
    ): ResponseEntity<ApiResponse<Nothing>> {
        postUseCase.delete(DeletePostCommand.of(id, username))
        return ApiResponse.success(HttpStatus.NO_CONTENT)
    }
}
