package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.UpdatePostCommand
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
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UpdatePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: UpdatePostService

    @Test
    fun `본인 게시글을 수정하면 수정된 결과를 반환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)
        val updated = Post.restore(1L, PostTitle.of("수정제목"), PostContent.of("수정내용"), "user1", LocalDateTime.now(), LocalDateTime.now())
        given(postPort.save(any())).willReturn(updated)

        val result = sut.update(UpdatePostCommand.of(1L, "수정제목", "수정내용", "user1"))

        assertThat(result.title).isEqualTo("수정제목")
        assertThat(result.content).isEqualTo("수정내용")
    }

    @Test
    fun `존재하지 않는 게시글 수정 시 NOT_FOUND 예외를 던진다`() {
        given(postPort.findById(999L)).willReturn(null)

        val exception = assertThrows<BaseException> {
            sut.update(UpdatePostCommand.of(999L, "제목", "내용", "user1"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.NOT_FOUND)
    }

    @Test
    fun `타인의 게시글 수정 시 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.findById(1L)).willReturn(post)

        val exception = assertThrows<BaseException> {
            sut.update(UpdatePostCommand.of(1L, "수정제목", "수정내용", "other"))
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }
}
