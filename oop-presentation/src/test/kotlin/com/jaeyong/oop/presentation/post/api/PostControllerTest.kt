package com.jaeyong.oop.presentation.post.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.GetPostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var sut: PostController
    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var createPostUseCase: CreatePostUseCase

    @Mock
    private lateinit var getPostUseCase: GetPostUseCase

    @Mock
    private lateinit var updatePostUseCase: UpdatePostUseCase

    @Mock
    private lateinit var deletePostUseCase: DeletePostUseCase

    private val now = LocalDateTime.of(2026, 4, 16, 0, 0)

    @BeforeEach
    fun setUp() {
        sut = PostController(createPostUseCase, getPostUseCase, updatePostUseCase, deletePostUseCase)
        mockMvc = MockMvcBuilders
            .standaloneSetup(sut)
            .setControllerAdvice(GlobalExceptionHandler())
            .setCustomArgumentResolvers(CurrentUserArgumentResolver())
            .build()
    }

    @Test
    @DisplayName("1. 정상 게시글 생성 시 201을 반환한다")
    fun `정상 게시글 생성 시 201을 반환한다`() {
        // given
        given(createPostUseCase.create(CreatePostCommand.of(title = "제목", content = "내용", authorId = 1L)))
            .willReturn(CreatePostResult.of(1L))
        val body = mapOf("title" to "제목", "content" to "내용")

        // when & then
        mockMvc.perform(
            post("/api/posts")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.postId").value(1))
    }

    @Test
    @DisplayName("2. 비로그인 게시글 생성 시 400과 A001을 반환한다")
    fun `비로그인 게시글 생성 시 400과 A001을 반환한다`() {
        // given
        val body = mapOf("title" to "제목", "content" to "내용")

        // when & then
        mockMvc.perform(
            post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A001"))
    }

    @Test
    @DisplayName("3. 게시글 단건 조회 시 200을 반환한다")
    fun `게시글 단건 조회 시 200을 반환한다`() {
        // given
        given(getPostUseCase.getById(GetPostCommand.of(1L)))
            .willReturn(GetPostResult.of(id = 1L, title = "제목", content = "내용", authorId = 1L, authorName = "작성자", createdAt = now, updatedAt = now))

        // when & then
        mockMvc.perform(get("/api/posts/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.title").value("제목"))
    }

    @Test
    @DisplayName("4. 게시글 목록 조회 시 200을 반환한다")
    fun `게시글 목록 조회 시 200을 반환한다`() {
        // given
        given(getPostUseCase.getAll())
            .willReturn(listOf(
                GetPostListResult.of(id = 1L, title = "제목", authorId = 1L, authorName = "작성자", createdAt = now)
            ))

        // when & then
        mockMvc.perform(get("/api/posts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data[0].id").value(1))
            .andExpect(jsonPath("$.data[0].title").value("제목"))
    }

    @Test
    @DisplayName("5. 정상 게시글 수정 시 200을 반환한다")
    fun `정상 게시글 수정 시 200을 반환한다`() {
        // given
        given(updatePostUseCase.update(UpdatePostCommand.of(postId = 1L, title = "수정제목", content = "수정내용", requesterId = 1L)))
            .willReturn(UpdatePostResult.of(id = 1L, title = "수정제목", content = "수정내용", authorId = 1L, authorName = "작성자", createdAt = now, updatedAt = now))
        val body = mapOf("title" to "수정제목", "content" to "수정내용")

        // when & then
        mockMvc.perform(
            put("/api/posts/1")
                .requestAttr("userId", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value("수정제목"))
    }

    @Test
    @DisplayName("6. 비로그인 게시글 수정 시 400과 A001을 반환한다")
    fun `비로그인 게시글 수정 시 400과 A001을 반환한다`() {
        // given
        val body = mapOf("title" to "수정제목", "content" to "수정내용")

        // when & then
        mockMvc.perform(
            put("/api/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A001"))
    }

    @Test
    @DisplayName("7. 정상 게시글 삭제 시 200을 반환한다")
    fun `정상 게시글 삭제 시 200을 반환한다`() {
        // given
        willDoNothing().given(deletePostUseCase).delete(DeletePostCommand.of(postId = 1L, requesterId = 1L))

        // when & then
        mockMvc.perform(
            delete("/api/posts/1")
                .requestAttr("userId", 1L)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
    }

    @Test
    @DisplayName("8. 비로그인 게시글 삭제 시 400과 A001을 반환한다")
    fun `비로그인 게시글 삭제 시 400과 A001을 반환한다`() {
        // when & then
        mockMvc.perform(delete("/api/posts/1"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A001"))
    }
}
