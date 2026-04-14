package com.jaeyong.oop.presentation.post.api

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.result.GetPostListResult
import com.jaeyong.oop.application.post.result.GetPostResult
import com.jaeyong.oop.application.post.result.UpdatePostResult
import com.jaeyong.oop.application.post.usecase.CreatePostUseCase
import com.jaeyong.oop.application.post.usecase.DeletePostUseCase
import com.jaeyong.oop.application.post.usecase.GetPostListUseCase
import com.jaeyong.oop.application.post.usecase.GetPostUseCase
import com.jaeyong.oop.application.post.usecase.UpdatePostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.BDDMockito.willThrow
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var sut: PostController
    private val objectMapper = ObjectMapper().registerModule(JavaTimeModule())

    @Mock private lateinit var createPostUseCase: CreatePostUseCase
    @Mock private lateinit var getPostUseCase: GetPostUseCase
    @Mock private lateinit var getPostListUseCase: GetPostListUseCase
    @Mock private lateinit var updatePostUseCase: UpdatePostUseCase
    @Mock private lateinit var deletePostUseCase: DeletePostUseCase

    private val now = LocalDateTime.now()

    @BeforeEach
    fun setUp() {
        sut = PostController(createPostUseCase, getPostUseCase, getPostListUseCase, updatePostUseCase, deletePostUseCase)
        mockMvc = MockMvcBuilders
            .standaloneSetup(sut)
            .setControllerAdvice(GlobalExceptionHandler())
            .setCustomArgumentResolvers(CurrentUserArgumentResolver())
            .build()
    }

    private fun authenticatedRequest(method: org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder, username: String = "user1"): org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder =
        method.requestAttr("username", username)

    @Test
    fun `게시글 작성 시 201을 반환한다`() {
        val result = CreatePostResult.of(Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", now, null))
        given(createPostUseCase.create(any())).willReturn(result)
        val body = mapOf("title" to "제목", "content" to "내용")

        mockMvc.perform(
            authenticatedRequest(post("/posts"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value("제목"))
    }

    @Test
    fun `게시글 단건 조회 시 200을 반환한다`() {
        val result = GetPostResult.of(Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", now, null))
        given(getPostUseCase.get(any())).willReturn(result)

        mockMvc.perform(get("/posts/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(1))
    }

    @Test
    fun `존재하지 않는 게시글 조회 시 400과 D001을 반환한다`() {
        given(getPostUseCase.get(any())).willThrow(BaseException(ErrorCode.NOT_FOUND))

        mockMvc.perform(get("/posts/999"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D001"))
    }

    @Test
    fun `게시글 목록 조회 시 200을 반환한다`() {
        val posts = listOf(Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", now, null))
        val result = GetPostListResult.of(posts, 1L, 0, 10)
        given(getPostListUseCase.getList(any())).willReturn(result)

        mockMvc.perform(get("/posts").param("page", "0").param("size", "10"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.posts[0].title").value("제목"))
            .andExpect(jsonPath("$.data.totalCount").value(1))
    }

    @Test
    fun `게시글 수정 시 200을 반환한다`() {
        val result = UpdatePostResult.of(Post.restore(1L, PostTitle.of("수정제목"), PostContent.of("수정내용"), "user1", now, now))
        given(updatePostUseCase.update(any())).willReturn(result)
        val body = mapOf("title" to "수정제목", "content" to "수정내용")

        mockMvc.perform(
            authenticatedRequest(put("/posts/1"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.title").value("수정제목"))
    }

    @Test
    fun `타인의 게시글 수정 시 400과 D005를 반환한다`() {
        given(updatePostUseCase.update(any())).willThrow(BaseException(ErrorCode.POST_ACCESS_DENIED))
        val body = mapOf("title" to "수정제목", "content" to "수정내용")

        mockMvc.perform(
            authenticatedRequest(put("/posts/1"), "other")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D005"))
    }

    @Test
    fun `게시글 삭제 시 204를 반환한다`() {
        willDoNothing().given(deletePostUseCase).delete(any())

        mockMvc.perform(authenticatedRequest(delete("/posts/1")))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `타인의 게시글 삭제 시 400과 D005를 반환한다`() {
        willThrow(BaseException(ErrorCode.POST_ACCESS_DENIED)).given(deletePostUseCase).delete(any())

        mockMvc.perform(authenticatedRequest(delete("/posts/1"), "other"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("D005"))
    }
}
