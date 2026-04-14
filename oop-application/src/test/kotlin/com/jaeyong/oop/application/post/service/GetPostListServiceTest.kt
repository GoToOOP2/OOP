package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostListCommand
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetPostListServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: GetPostListService

    @Test
    fun `게시글 목록을 조회하면 페이지 정보와 함께 반환한다`() {
        val posts = listOf(
            Post.restore(1L, PostTitle.of("제목1"), PostContent.of("내용1"), "user1", LocalDateTime.now(), null),
            Post.restore(2L, PostTitle.of("제목2"), PostContent.of("내용2"), "user2", LocalDateTime.now(), null)
        )
        given(postPort.findAll(0, 10)).willReturn(posts)
        given(postPort.countAll()).willReturn(2L)

        val result = sut.getList(GetPostListCommand.of(0, 10))

        assertThat(result.posts).hasSize(2)
        assertThat(result.totalCount).isEqualTo(2L)
        assertThat(result.page).isEqualTo(0)
        assertThat(result.size).isEqualTo(10)
    }

    @Test
    fun `게시글이 없으면 빈 목록을 반환한다`() {
        given(postPort.findAll(0, 10)).willReturn(emptyList())
        given(postPort.countAll()).willReturn(0L)

        val result = sut.getList(GetPostListCommand.of(0, 10))

        assertThat(result.posts).isEmpty()
        assertThat(result.totalCount).isEqualTo(0L)
    }
}
