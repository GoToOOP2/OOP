package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.common.CreatePostCommand
import com.jaeyong.oop.application.post.common.DeletePostCommand
import com.jaeyong.oop.application.post.common.GetPostCommand
import com.jaeyong.oop.application.post.common.UpdatePostCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.port.PostPort
import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserQueryPort
import com.navercorp.fixturemonkey.FixtureMonkey
import com.navercorp.fixturemonkey.kotlin.introspector.PrimaryConstructorArbitraryIntrospector
import com.navercorp.fixturemonkey.kotlin.giveMeKotlinBuilder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any

@ExtendWith(MockitoExtension::class)
class PostServiceTest {

    @Mock
    private lateinit var postPort: PostPort

    @Mock
    private lateinit var userQueryPort: UserQueryPort

    @InjectMocks
    private lateinit var sut: PostService

    private val fixtureMonkey = FixtureMonkey.builder()
        .objectIntrospector(PrimaryConstructorArbitraryIntrospector.INSTANCE)
        .build()

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("1. 정상 생성 → CreatePostResult 반환")
        fun `정상 생성 시 CreatePostResult를 반환한다`() {
            // given
            val command = CreatePostCommand.of(title = "제목", content = "내용", userId = 10L)
            val saved = fixtureMonkey.giveMeKotlinBuilder<Post>()
                .set(Post::id, 1L)
                .set(Post::authorId, 10L)
                .sample()
            given(postPort.store(any())).willReturn(saved)

            // when
            val result = sut.create(command)

            // then
            assertThat(result.postId).isEqualTo(1L)
        }
    }

    @Nested
    @DisplayName("getById")
    inner class GetById {

        @Test
        @DisplayName("2. 정상 단건 조회 → GetPostResult 반환")
        fun `정상 단건 조회 시 GetPostResult를 반환한다`() {
            // given
            val post = fixtureMonkey.giveMeKotlinBuilder<Post>()
                .set(Post::id, 1L)
                .set(Post::authorId, 10L)
                .sample()
            val user = fixtureMonkey.giveMeKotlinBuilder<User>()
                .set(User::id, 10L)
                .sample()
            given(postPort.getActiveById(1L)).willReturn(post)
            given(userQueryPort.findById(10L)).willReturn(user)

            // when
            val result = sut.getById(GetPostCommand.of(1L))

            // then
            assertThat(result.id).isEqualTo(1L)
            assertThat(result.title).isEqualTo(post.title.value)
            assertThat(result.authorName).isEqualTo(user.username.value)
        }

        @Test
        @DisplayName("3. 게시글 없음 → NOT_FOUND 예외")
        fun `존재하지 않는 게시글 조회 시 NOT_FOUND 예외를 던진다`() {
            // given
            given(postPort.getActiveById(99L)).willReturn(null)

            // when & then
            val exception = assertThrows<BaseException> { sut.getById(GetPostCommand.of(99L)) }
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
        }

        @Test
        @DisplayName("4. 작성자 없음 → NOT_FOUND 예외")
        fun `작성자가 존재하지 않으면 NOT_FOUND 예외를 던진다`() {
            // given
            val post = fixtureMonkey.giveMeKotlinBuilder<Post>()
                .set(Post::id, 1L)
                .set(Post::authorId, 10L)
                .sample()
            given(postPort.getActiveById(1L)).willReturn(post)
            given(userQueryPort.findById(10L)).willReturn(null)

            // when & then
            val exception = assertThrows<BaseException> { sut.getById(GetPostCommand.of(1L)) }
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("getAll")
    inner class GetAll {

        @Test
        @DisplayName("5. 정상 목록 조회 → List<GetPostListResult> 반환")
        fun `정상 목록 조회 시 GetPostListResult 목록을 반환한다`() {
            // given
            val posts = listOf(
                fixtureMonkey.giveMeKotlinBuilder<Post>().set(Post::id, 1L).sample(),
                fixtureMonkey.giveMeKotlinBuilder<Post>().set(Post::id, 2L).sample()
            )
            given(postPort.getAllActive()).willReturn(posts)

            // when
            val result = sut.getAll()

            // then
            assertThat(result).hasSize(2)
            assertThat(result[0].id).isEqualTo(1L)
            assertThat(result[1].id).isEqualTo(2L)
        }

        @Test
        @DisplayName("6. 빈 목록 → 빈 리스트 반환")
        fun `게시글이 없으면 빈 리스트를 반환한다`() {
            // given
            given(postPort.getAllActive()).willReturn(emptyList())

            // when
            val result = sut.getAll()

            // then
            assertThat(result).isEmpty()
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("7. 정상 수정 → UpdatePostResult 반환")
        fun `정상 수정 시 UpdatePostResult를 반환한다`() {
            // given
            val post = fixtureMonkey.giveMeKotlinBuilder<Post>()
                .set(Post::id, 1L)
                .set(Post::authorId, 10L)
                .sample()
            val user = fixtureMonkey.giveMeKotlinBuilder<User>()
                .set(User::id, 10L)
                .sample()
            val command = UpdatePostCommand.of(postId = 1L, title = "새제목", content = "새내용", userId = 10L)
            given(postPort.getActiveById(1L)).willReturn(post)
            given(postPort.store(post)).willReturn(post)
            given(userQueryPort.findById(10L)).willReturn(user)

            // when
            val result = sut.update(command)

            // then
            assertThat(result.id).isEqualTo(1L)
            assertThat(result.authorName).isEqualTo(user.username.value)
        }

        @Test
        @DisplayName("8. 수정 대상 게시글 없음 → NOT_FOUND 예외")
        fun `수정 대상 게시글이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val command = UpdatePostCommand.of(postId = 99L, title = "제목", content = "내용", userId = 10L)
            given(postPort.getActiveById(99L)).willReturn(null)

            // when & then
            val exception = assertThrows<BaseException> { sut.update(command) }
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("9. 정상 삭제")
        fun `정상 삭제 시 postPort_save가 호출된다`() {
            // given
            val post = fixtureMonkey.giveMeKotlinBuilder<Post>()
                .set(Post::id, 1L)
                .set(Post::authorId, 10L)
                .set(Post::deleted, false)
                .sample()
            val command = DeletePostCommand.of(postId = 1L, userId = 10L)
            given(postPort.getActiveById(1L)).willReturn(post)
            given(postPort.store(post)).willReturn(post)

            // when
            sut.delete(command)

            // then
            verify(postPort).save(post)
        }

        @Test
        @DisplayName("10. 삭제 대상 게시글 없음 → NOT_FOUND 예외")
        fun `삭제 대상 게시글이 없으면 NOT_FOUND 예외를 던진다`() {
            // given
            val command = DeletePostCommand.of(postId = 99L, userId = 10L)
            given(postPort.getActiveById(99L)).willReturn(null)

            // when & then
            val exception = assertThrows<BaseException> { sut.delete(command) }
            assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
        }
    }
}
