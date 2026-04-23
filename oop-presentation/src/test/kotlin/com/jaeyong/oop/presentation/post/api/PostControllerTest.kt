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
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.introspector.PrimaryConstructorArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
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

@ExtendWith(MockitoExtension::class)
class PostControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var sut: PostController
    private val objectMapper = ObjectMapper()

    @Mock
    private lateinit var postUseCase: PostUseCase

    private val fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(PrimaryConstructorArbitraryIntrospector.INSTANCE)
        .build()

    @BeforeEach
    fun setUp() {
        sut = PostController(postUseCase)
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
        val createResult = fixtureMonkey.giveMeKotlinBuilder<CreatePostResult>()
            .set(CreatePostResult::postId, 1L)
            .sample()
        given(postUseCase.create(CreatePostCommand.of(title = "제목", content = "내용", userId = 1L)))
            .willReturn(createResult)
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
        val getResult = fixtureMonkey.giveMeKotlinBuilder<GetPostResult>()
            .set(GetPostResult::id, 1L)
            .set(GetPostResult::title, "제목")
            .sample()
        given(postUseCase.getById(GetPostCommand.of(1L)))
            .willReturn(getResult)

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
        val listResult = fixtureMonkey.giveMeKotlinBuilder<GetPostListResult>()
            .set(GetPostListResult::id, 1L)
            .set(GetPostListResult::title, "제목")
            .sample()
        given(postUseCase.getAll())
            .willReturn(listOf(listResult))

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
        val updateResult = fixtureMonkey.giveMeKotlinBuilder<UpdatePostResult>()
            .set(UpdatePostResult::id, 1L)
            .set(UpdatePostResult::title, "수정제목")
            .sample()
        given(postUseCase.update(UpdatePostCommand.of(postId = 1L, title = "수정제목", content = "수정내용", userId = 1L)))
            .willReturn(updateResult)
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
        willDoNothing().given(postUseCase).delete(DeletePostCommand.of(postId = 1L, userId = 1L))

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
