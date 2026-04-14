package com.jaeyong.oop.application.post.service

import com.jaeyong.oop.application.post.command.CreatePostCommand
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
import org.mockito.kotlin.any
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CreatePostServiceTest {

    @Mock private lateinit var postPort: PostPort
    @InjectMocks private lateinit var sut: CreatePostService

    @Test
    fun `게시글을 작성하면 저장된 결과를 반환한다`() {
        val command = CreatePostCommand.of("제목", "내용", "user1")
        val saved = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)
        given(postPort.save(any())).willReturn(saved)

        val result = sut.create(command)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title).isEqualTo("제목")
        assertThat(result.authorUsername).isEqualTo("user1")
    }
}
