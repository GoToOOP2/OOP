package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PostTest {

    private val title = TitleVO.from("제목")
    private val content = ContentVO.from("내용")
    private val authorId = 1L

    @Nested
    @DisplayName("create")
    inner class Create {

        @Test
        @DisplayName("1. 게시글이 정상적으로 생성된다")
        fun `게시글이 정상적으로 생성된다`() {
            // when
            val post = Post.create(title, content, authorId)

            // then
            assertThat(post.id).isNull()
            assertThat(post.title).isEqualTo(title)
            assertThat(post.content).isEqualTo(content)
            assertThat(post.authorId).isEqualTo(authorId)
            assertThat(post.deleted).isFalse()
        }
    }

    @Nested
    @DisplayName("restore")
    inner class Restore {

        @Test
        @DisplayName("2. DB 데이터로 게시글이 복원된다")
        fun `DB 데이터로 게시글이 복원된다`() {
            // given
            val now = LocalDateTime.of(2026, 1, 1, 0, 0)

            // when
            val post = Post.restore(
                id = 10L,
                title = title,
                content = content,
                authorId = authorId,
                deleted = false,
                createdAt = now,
                updatedAt = now
            )

            // then
            assertThat(post.id).isEqualTo(10L)
            assertThat(post.title).isEqualTo(title)
            assertThat(post.content).isEqualTo(content)
            assertThat(post.authorId).isEqualTo(authorId)
            assertThat(post.deleted).isFalse()
            assertThat(post.createdAt).isEqualTo(now)
            assertThat(post.updatedAt).isEqualTo(now)
        }
    }

    @Nested
    @DisplayName("update")
    inner class Update {

        @Test
        @DisplayName("3-1. 작성자가 게시글을 수정할 수 있다")
        fun `작성자가 게시글을 수정할 수 있다`() {
            // given
            val post = Post.create(title, content, authorId)
            val newTitle = TitleVO.from("수정된 제목")
            val newContent = ContentVO.from("수정된 내용")

            // when
            post.update(newTitle, newContent, authorId)

            // then
            assertThat(post.title).isEqualTo(newTitle)
            assertThat(post.content).isEqualTo(newContent)
        }

        @Test
        @DisplayName("3-2. 작성자가 아니면 수정 시 예외가 발생한다")
        fun `작성자가 아니면 수정 시 예외가 발생한다`() {
            // given
            val post = Post.create(title, content, authorId)
            val otherId = 999L

            // when & then
            assertThatThrownBy { post.update(title, content, otherId) }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED)
        }
    }

    @Nested
    @DisplayName("delete")
    inner class Delete {

        @Test
        @DisplayName("4-1. 작성자가 게시글을 삭제할 수 있다")
        fun `작성자가 게시글을 삭제할 수 있다`() {
            // given
            val post = Post.create(title, content, authorId)

            // when
            post.delete(authorId)

            // then
            assertThat(post.deleted).isTrue()
        }

        @Test
        @DisplayName("4-2. 작성자가 아니면 삭제 시 예외가 발생한다")
        fun `작성자가 아니면 삭제 시 예외가 발생한다`() {
            // given
            val post = Post.create(title, content, authorId)
            val otherId = 999L

            // when & then
            assertThatThrownBy { post.delete(otherId) }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.ACCESS_DENIED)
        }

        @Test
        @DisplayName("4-3. 이미 삭제된 게시글을 다시 삭제하면 예외가 발생한다")
        fun `이미 삭제된 게시글을 다시 삭제하면 예외가 발생한다`() {
            // given
            val post = Post.create(title, content, authorId)
            post.delete(authorId)

            // when & then
            assertThatThrownBy { post.delete(authorId) }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.INVALID_STATE)
        }
    }

    @Nested
    @DisplayName("VO 검증")
    inner class VoValidation {

        @Test
        @DisplayName("5-1. 빈 제목으로 TitleVO 생성 시 TITLE_BLANK 예외가 발생한다")
        fun `빈 제목으로 TitleVO 생성 시 TITLE_BLANK 예외가 발생한다`() {
            assertThatThrownBy { TitleVO.from("   ") }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.TITLE_BLANK)
        }

        @Test
        @DisplayName("5-2. 255자 초과 제목으로 TitleVO 생성 시 TITLE_TOO_LONG 예외가 발생한다")
        fun `255자 초과 제목으로 TitleVO 생성 시 TITLE_TOO_LONG 예외가 발생한다`() {
            assertThatThrownBy { TitleVO.from("a".repeat(256)) }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.TITLE_TOO_LONG)
        }

        @Test
        @DisplayName("5-3. 빈 내용으로 ContentVO 생성 시 CONTENT_BLANK 예외가 발생한다")
        fun `빈 내용으로 ContentVO 생성 시 CONTENT_BLANK 예외가 발생한다`() {
            assertThatThrownBy { ContentVO.from("   ") }
                .isInstanceOf(BaseException::class.java)
                .extracting("errorCode").isEqualTo(ErrorCode.CONTENT_BLANK)
        }
    }
}
