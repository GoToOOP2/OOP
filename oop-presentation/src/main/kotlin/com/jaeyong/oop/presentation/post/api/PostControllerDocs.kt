package com.jaeyong.oop.presentation.post.api

import com.jaeyong.oop.presentation.post.request.CreatePostRequest
import com.jaeyong.oop.presentation.post.request.UpdatePostRequest
import com.jaeyong.oop.presentation.post.response.CreatePostResponse
import com.jaeyong.oop.presentation.post.response.GetPostResponse
import com.jaeyong.oop.presentation.post.response.UpdatePostResponse
import com.jaeyong.oop.presentation.response.ApiResponse
import com.jaeyong.oop.presentation.response.PageResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity

@Tag(name = "Post", description = "게시글 CRUD API")
interface PostControllerDocs {

    @Operation(summary = "게시글 생성", description = "제목과 내용을 받아 게시글을 생성합니다. JWT 인증이 필요합니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "게시글 생성 성공, 생성된 게시글 id 반환"),
        SwaggerApiResponse(responseCode = "400", description = "요청 값 유효성 검사 실패"),
        SwaggerApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 만료)")
    )
    fun create(
        request: CreatePostRequest,
        @Parameter(hidden = true) username: String
    ): ResponseEntity<ApiResponse<CreatePostResponse>>

    @Operation(summary = "게시글 단건 조회", description = "id로 게시글 하나를 조회합니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공"),
        SwaggerApiResponse(responseCode = "404", description = "게시글 없음")
    )
    fun get(
        @Parameter(description = "게시글 id") id: Long
    ): ResponseEntity<ApiResponse<GetPostResponse>>

    @Operation(summary = "게시글 목록 조회", description = "페이징과 정렬 방향을 지정해 게시글 목록을 조회합니다. 기본값: page=0, size=10, direction=DESC(최신순).")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "조회 성공")
    )
    fun getList(
        @Parameter(description = "페이지 번호 (0부터 시작)") page: Int,
        @Parameter(description = "페이지 크기") size: Int,
        @Parameter(description = "정렬 방향 (ASC / DESC)") direction: String
    ): ResponseEntity<ApiResponse<PageResponse<GetPostResponse>>>

    @Operation(summary = "게시글 수정", description = "게시글 제목과 내용을 수정합니다. 작성자 본인만 수정할 수 있습니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "수정 성공"),
        SwaggerApiResponse(responseCode = "400", description = "요청 값 유효성 검사 실패"),
        SwaggerApiResponse(responseCode = "401", description = "인증 실패"),
        SwaggerApiResponse(responseCode = "403", description = "작성자가 아닌 사용자의 수정 시도"),
        SwaggerApiResponse(responseCode = "404", description = "게시글 없음")
    )
    fun update(
        @Parameter(description = "게시글 id") id: Long,
        request: UpdatePostRequest,
        @Parameter(hidden = true) username: String
    ): ResponseEntity<ApiResponse<UpdatePostResponse>>

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다. 작성자 본인만 삭제할 수 있습니다.")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "204", description = "삭제 성공"),
        SwaggerApiResponse(responseCode = "401", description = "인증 실패"),
        SwaggerApiResponse(responseCode = "403", description = "작성자가 아닌 사용자의 삭제 시도"),
        SwaggerApiResponse(responseCode = "404", description = "게시글 없음")
    )
    fun delete(
        @Parameter(description = "게시글 id") id: Long,
        @Parameter(hidden = true) username: String
    ): ResponseEntity<ApiResponse<Nothing>>
}
