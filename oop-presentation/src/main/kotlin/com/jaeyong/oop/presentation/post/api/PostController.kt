package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.GetPostCommand
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
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 게시글 CRUD API 컨트롤러.
 */
@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/api/posts")
class PostController(
    private val createPostUseCase: CreatePostUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val updatePostUseCase: UpdatePostUseCase,
    private val deletePostUseCase: DeletePostUseCase
) {

    /**
     * 게시글 생성 — 인증된 사용자가 제목과 본문을 입력하여 게시글을 등록한다.
     *
     * @param userId 인증된 사용자 ID
     * @param request 제목, 본문
     * @return 201 Created, body: 생성된 게시글 ID
     */
    @Operation(summary = "게시글 생성", description = "인증된 사용자가 제목과 본문을 입력하여 게시글을 등록한다")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "생성 성공"),
        SwaggerApiResponse(responseCode = "400", description = "인증 실패 (A001)")
    )
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

    /**
     * 게시글 목록 조회 — 전체 게시글을 요약 형태로 반환한다.
     *
     * @return 200 OK, body: 게시글 목록
     */
    @Operation(summary = "게시글 목록 조회", description = "삭제되지 않은 전체 게시글을 요약 형태로 반환한다")
    @SwaggerApiResponse(responseCode = "200", description = "조회 성공")
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

    /**
     * 게시글 상세 조회 — ID로 게시글의 전체 정보를 반환한다.
     *
     * @param id 게시글 ID
     * @return 200 OK, body: 게시글 상세
     */
    @Operation(summary = "게시글 단건 조회", description = "ID로 게시글의 전체 정보를 반환한다")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
        SwaggerApiResponse(responseCode = "400", description = "게시글 없음 (D001)")
    )
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<ApiResponse<PostDetailResponse>> {
        val result = getPostUseCase.getById(GetPostCommand.of(id))
        val response = PostDetailResponse.of(
            id = result.id, title = result.title, content = result.content,
            authorId = result.authorId, authorName = result.authorName,
            createdAt = result.createdAt, updatedAt = result.updatedAt
        )
        return ApiResponse.success(response)
    }

    /**
     * 게시글 수정 — 인증된 사용자가 자신의 게시글 제목과 본문을 수정한다.
     *
     * @param id 게시글 ID
     * @param userId 인증된 사용자 ID
     * @param request 제목, 본문
     * @return 200 OK, body: 수정된 게시글 상세
     */
    @Operation(summary = "게시글 수정", description = "인증된 사용자가 자신의 게시글 제목과 본문을 수정한다")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "수정 성공"),
        SwaggerApiResponse(responseCode = "400", description = "게시글 없음 (D001) / 권한 없음 (A003) / 인증 실패 (A001)")
    )
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

    /**
     * 게시글 삭제 — 인증된 사용자가 자신의 게시글을 삭제한다.
     *
     * @param id 게시글 ID
     * @param userId 인증된 사용자 ID
     * @return 200 OK
     */
    @Operation(summary = "게시글 삭제", description = "인증된 사용자가 자신의 게시글을 소프트 삭제한다")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "삭제 성공"),
        SwaggerApiResponse(responseCode = "400", description = "게시글 없음 (D001) / 권한 없음 (A003) / 인증 실패 (A001)")
    )
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
