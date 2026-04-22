package com.jaeyong.oop.domain.post

import com.jaeyong.oop.common.exception.BaseException
import com.jaeyong.oop.common.exception.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

@DisplayName("Post")
class PostTest {

    private val title = "테스트 제목"
    private val content = "테스트 내용"
    private val author = "user1"

    @Test
    @DisplayName("create - 정상 생성 시 id null, createdAt 설정됨")
    fun createPost() {
        // given & when
        val sut = Post.create(title, content, author)

        // then
        assertThat(sut.id).isNull()
        assertThat(sut.createdAt).isNotNull()
        assertThat(sut.updatedAt).isNull()
    }

    @Test
    @DisplayName("restore - DB 복원 시 id, createdAt, updatedAt 그대로 유지")
    fun restorePost() {
        // given
        val createdAt = LocalDateTime.of(2024, 1, 1, 0, 0)
        val updatedAt = LocalDateTime.of(2024, 1, 2, 0, 0)

        // when
        val sut = Post.restore(1L, title, content, author, createdAt, updatedAt)

        // then
        assertThat(sut.id).isEqualTo(1L)
        assertThat(sut.createdAt).isEqualTo(createdAt)
        assertThat(sut.updatedAt).isEqualTo(updatedAt)
    }

    @Test
    @DisplayName("validateOwner - 본인이면 예외 없음")
    fun validateOwnerSuccess() {
        // given
        val sut = Post.create(title, content, author)

        // when & then
        assertThatNoException().isThrownBy { sut.validateOwner(author) }
    }

    @Test
    @DisplayName("validateOwner - 타인이면 POST_ACCESS_DENIED")
    fun validateOwnerFail() {
        // given
        val sut = Post.create(title, content, author)

        // when & then
        val ex = assertThrows<BaseException> { sut.validateOwner("other") }
        assertThat(ex.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }

    @Test
    @DisplayName("update - 정상 수정 시 새 객체 반환, updatedAt 설정됨")
    fun updatePostSuccess() {
        // given
        val sut = Post.restore(1L, title, content, author, LocalDateTime.now(), null)
        // when
        val updated = sut.update("수정된 제목", "수정된 내용", author)

        // then
        assertThat(updated.updatedAt).isNotNull()
        assertThat(updated).isNotSameAs(sut)
    }

    @Test
    @DisplayName("update - 타인이 수정하면 POST_ACCESS_DENIED")
    fun updatePostFail() {
        // given
        val sut = Post.restore(1L, title, content, author, LocalDateTime.now(), null)

        // when & then
        val ex = assertThrows<BaseException> {
            sut.update("수정", "수정 내용", "other")
        }
        assertThat(ex.errorCode).isEqualTo(ErrorCode.POST_ACCESS_DENIED)
    }
}
