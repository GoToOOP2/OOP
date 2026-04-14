package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.DeletePostCommand
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
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class DeletePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: DeletePostService

    @Test
    fun `본인 게시글을 삭제하면 정상 삭제된다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        sut.delete(DeletePostCommand.of(1L, "user1"))

        then(postPort).should().deleteById(1L)
    }

    @Test
    fun `존재하지 않는 게시글 삭제 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.delete(DeletePostCommand.of(999L, "user1"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }

    @Test
    fun `타인의 게시글 삭제 시 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        val exception = assertThrows<BaseException> {
            sut.delete(DeletePostCommand.of(1L, "other"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }
}
