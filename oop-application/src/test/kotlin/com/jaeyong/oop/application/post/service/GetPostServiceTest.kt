package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.GetPostCommand
import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.domain.post.port.PostPort
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class GetPostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: GetPostService

    @Test
    fun `존재하는 게시글을 조회하면 결과를 반환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        val result = sut.get(GetPostCommand.of(1L))

        assertThat(result.id).isEqualTo(1L)
    }

    @Test
    fun `존재하지 않는 게시글 조회 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> { sut.get(GetPostCommand.of(999L)) }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }
}
