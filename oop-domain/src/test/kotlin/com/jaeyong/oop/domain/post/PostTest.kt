package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class PostTest {

    private val title = PostTitle.of("제목")
    private val content = PostContent.of("내용")
    private val author = "user1"

    @Test
    fun `create로 새 게시글을 생성한다`() {
        val post = Post.create(title, content, author)

        assertThat(post.id).isNull()
        assertThat(post.title).isEqualTo(title)
        assertThat(post.content).isEqualTo(content)
        assertThat(post.authorUsername).isEqualTo(author)
        assertThat(post.updatedAt).isNull()
    }

    @Test
    fun `restore로 기존 게시글을 복원한다`() {
        val createdAt = LocalDateTime.now()
        val post = Post.restore(1L, title, content, author, createdAt, null)

        assertThat(post.id).isEqualTo(1L)
        assertThat(post.createdAt).isEqualTo(createdAt)
    }

    @Test
    fun `작성자가 수정하면 title과 content가 변경된다`() {
        val post = Post.create(title, content, author)
        val newTitle = PostTitle.of("새 제목")
        val newContent = PostContent.of("새 내용")

        val updated = post.update(newTitle, newContent, author)

        assertThat(updated.title).isEqualTo(newTitle)
        assertThat(updated.content).isEqualTo(newContent)
        assertThat(updated.updatedAt).isNotNull()
    }

    @Test
    fun `작성자가 아닌 사람이 수정하면 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.create(title, content, author)

        val exception = assertThrows<BaseException> {
            post.update(PostTitle.of("새 제목"), PostContent.of("새 내용"), "other")
        }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }

    @Test
    fun `작성자가 아닌 사람이 validateOwner 호출하면 POST_ACCESS_DENIED 예외를 던진다`() {
        val post = Post.create(title, content, author)

        val exception = assertThrows<BaseException> { post.validateOwner("other") }
        assertThat(exception.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }

    @Test
    fun `작성자가 validateOwner 호출하면 예외를 던지지 않는다`() {
        val post = Post.create(title, content, author)
        post.validateOwner(author)
    }
}
