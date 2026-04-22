package com.jaeyong.oop.presentation.post.api

/**
 * [PR 논의 사항 - 테스트 전략에 대하여]
 *
 * - 현재 @WebMvcTest를 수행하기 위해 작성된 부가적인 설정 코드(TestMvcConfig, TestApplication 등)가
 *   테스트의 본질보다 더 많은 비중을 차지하고 있어 관리가 어렵게 느껴집니다.
 * - 이러한 웹 계층 단위 테스트보다 통합 테스트(@SpringBootTest)를 작성할 때,
 *   별도의 가짜 설정 없이 실제 환경과 동일한 흐름으로 더 가독성 있고 수월하게 테스트를 진행할 수 있을 것 같습니다.
 * - 컨트롤러 단위 테스트를 통합 테스트로 대체하는 방향에 대해 팀원분들의 의견이 궁금합니다.
 */

import com.fasterxml.jackson.databind.ObjectMapper
import com.jaeyong.oop.application.post.command.CreatePostCommand
import com.jaeyong.oop.application.post.result.CreatePostResult
import com.jaeyong.oop.application.post.usecase.PostUseCase
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.presentation.auth.CurrentUserArgumentResolver
import com.jaeyong.oop.presentation.exception.GlobalExceptionHandler
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

/**
 * PostController 단위 테스트 — POST /posts 엔드포인트
 *
 * @WebMvcTest: MVC 레이어(Controller, Filter, ArgumentResolver)만 로드한다.
 *   실제 DB·Service 빈은 띄우지 않으므로 PostUseCase를 MockBean으로 대체한다.
 *
 * @Import(GlobalExceptionHandler): @WebMvcTest는 기본적으로 @ControllerAdvice를
 *   자동 등록하지 않으므로 명시적으로 임포트해야 에러 응답 body를 검증할 수 있다.
 *
 * @Import(TestMvcConfig): WebConfig(ArgumentResolver 등록)가 oop-boot 모듈에 있어
 *   @WebMvcTest 컨텍스트에서 로드되지 않는다. TestMvcConfig로 CurrentUserArgumentResolver를
 *   직접 등록해 실제 운영과 동일한 인증 흐름을 재현한다.
 */
@WebMvcTest(PostController::class)
@Import(GlobalExceptionHandler::class, PostControllerTest.TestMvcConfig::class)
@DisplayName("PostController")
class PostControllerTest {

    /**
     * @WebMvcTest의 컴포넌트 스캔 기준점.
     *
     * @WebMvcTest는 내부적으로 @SpringBootApplication을 찾아 스캔 범위를 결정한다.
     * 테스트 모듈(oop-presentation)에는 @SpringBootApplication이 없으므로
     * 여기에 더미 클래스를 선언해 스캔 기준을 이 패키지로 고정한다.
     */
    @SpringBootApplication
    class TestApplication

    /**
     * CurrentUserArgumentResolver 등록용 테스트 전용 MVC 설정.
     *
     * 운영 코드의 WebConfig(oop-boot)가 @WebMvcTest 컨텍스트에 포함되지 않기 때문에
     * CurrentUserArgumentResolver가 등록되지 않는다. @TestConfiguration으로
     * 테스트 컨텍스트에만 적용되는 WebMvcConfigurer를 추가해 이를 해결한다.
     */
    @TestConfiguration
    class TestMvcConfig : WebMvcConfigurer {
        override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
            resolvers.add(CurrentUserArgumentResolver())
        }
    }

    @Autowired lateinit var mockMvc: MockMvc
    @Autowired lateinit var objectMapper: ObjectMapper

    /** PostService 실제 구현 대신 Mock을 주입 — 외부 의존성 없이 Controller 로직만 검증한다. */
    @MockBean lateinit var postUseCase: PostUseCase

    /** 매직 스트링 방지용 상수 — 테스트 전체에서 동일한 값을 공유한다. */
    companion object {
        const val TITLE = "테스트 제목"
        const val CONTENT = "테스트 내용"
        const val USERNAME = "user1"
        const val POST_ID = 1L
    }

    /**
     * 테스트 오브젝트 팩토리 — CreatePostResult 생성.
     *
     * 기본값을 제공하면서 필요한 필드만 오버라이드할 수 있어
     * 각 테스트가 관심 없는 값을 반복 선언할 필요가 없다.
     * Post.reconstruct()를 통해 DB 복원 경로를 재현하고, CreatePostResult.from()로 래핑한다.
     */
    private fun createPostResult(id: Long = POST_ID) = CreatePostResult.from(
        Post.reconstruct(id, TITLE, CONTENT, USERNAME, LocalDateTime.of(2024, 1, 1, 0, 0), null)
    )

    /**
     * 테스트 오브젝트 팩토리 — HTTP 요청 바디 JSON 문자열 생성.
     *
     * ObjectMapper로 직렬화해 실제 HTTP 요청과 동일한 형식을 보장한다.
     */
    private fun requestBody(title: String = TITLE, content: String = CONTENT) =
        objectMapper.writeValueAsString(mapOf("title" to title, "content" to content))

    @Test
    @DisplayName("POST /posts - 정상 작성 시 201과 게시글 정보를 반환한다")
    fun createPost() {
        // given
        // argumentCaptor: UseCase에 전달된 Command 객체를 캡처해 내용을 검증한다.
        val captor = argumentCaptor<CreatePostCommand>()
        whenever(postUseCase.create(any())).thenReturn(createPostResult())

        // when & then
        // requestAttr("username"): JwtAuthFilter가 검증 후 세팅하는 attribute를 테스트에서 직접 주입한다.
        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody())
                .requestAttr("username", USERNAME)
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.code").value("SUCCESS"))
            .andExpect(jsonPath("$.data.id").value(POST_ID))

        // Command 올바르게 만들어서 넘겼는지 검증
        verify(postUseCase).create(captor.capture())
        assertThat(captor.firstValue.title).isEqualTo(TITLE)
        assertThat(captor.firstValue.content).isEqualTo(CONTENT)
        assertThat(captor.firstValue.authorUsername).isEqualTo(USERNAME)
    }

    @Test
    @DisplayName("POST /posts - 제목 공백이면 400 VALIDATION_ERROR를 반환한다")
    fun createPostBlankTitle() {
        // @Valid 검증 실패 시 GlobalExceptionHandler가 C001을 반환하는지 확인한다.
        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(title = ""))
                .requestAttr("username", USERNAME)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("C001"))
    }

    @Test
    @DisplayName("POST /posts - 내용 공백이면 400 VALIDATION_ERROR를 반환한다")
    fun createPostBlankContent() {
        // @Valid 검증 실패 시 GlobalExceptionHandler가 C001을 반환하는지 확인한다.
        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody(content = ""))
                .requestAttr("username", USERNAME)
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("C001"))
    }

    @Test
    @DisplayName("POST /posts - JWT 없으면 400 UNAUTHORIZED를 반환한다")
    fun createPostUnauthorized() {
        // requestAttr("username") 없이 요청 → CurrentUserArgumentResolver가 UNAUTHORIZED 예외를 던진다.
        mockMvc.perform(
            post("/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody())
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.code").value("A001"))
    }
}
