package com.jaeyong.oop.infrastructure.post.entity

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDateTime



@DisplayName("PostEntity")
class PostEntityTest {

    /** 테스트 오브젝트 팩토리 — 기본값 제공, 필요한 필드만 오버라이드 */
    private fun entity(
        id: Long? = 1L,
        title: String = "테스트 제목",
        content: String = "테스트 내용",
        authorUsername: String = "user1",
        createdAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0),
        updatedAt: LocalDateTime? = LocalDateTime.of(2024, 1, 2, 0, 0)
    ) = PostEntity(id = id, title = title, content = content, authorUsername = authorUsername, createdAt = createdAt, updatedAt = updatedAt)

    /** 테스트 오브젝트 팩토리 — 기본값 제공, 필요한 필드만 오버라이드 */
    private fun post(
        id: Long? = 1L,
        title: String = "테스트 제목",
        content: String = "테스트 내용",
        authorUsername: String = "user1",
        createdAt: LocalDateTime = LocalDateTime.of(2024, 1, 1, 0, 0),
        updatedAt: LocalDateTime? = LocalDateTime.of(2024, 1, 2, 0, 0)
    ) = Post.restore(id = id, title = PostTitle.of(title), content = PostContent.of(content), authorUsername = authorUsername, createdAt = createdAt, updatedAt = updatedAt)

    @Test
    @DisplayName("toDomain - 모든 필드가 Post 도메인 객체로 정확히 매핑된다")
    fun toDomain() {
        // given
        val sut = entity()

        // when
        val post = sut.toDomain()

        // then
        assertThat(post.id).isEqualTo(sut.id)
        assertThat(post.titleValue).isEqualTo(sut.title)
        assertThat(post.contentValue).isEqualTo(sut.content)
        assertThat(post.authorUsername).isEqualTo(sut.authorUsername)
        assertThat(post.createdAt).isEqualTo(sut.createdAt)
        assertThat(post.updatedAt).isEqualTo(sut.updatedAt)
    }

    @Test
    @DisplayName("toDomain - updatedAt이 null이어도 정상 변환된다")
    fun toDomainWithNullUpdatedAt() {
        // given
        val sut = entity(updatedAt = null)

        // when
        val post = sut.toDomain()

        // then
        assertThat(post.updatedAt).isNull()
    }

    @Test
    @DisplayName("fromDomain - 모든 필드가 PostEntity로 정확히 매핑된다")
    fun fromDomain() {
        // given
        val post = post()

        // when
        val sut = PostEntity.fromDomain(post)

        // then
        assertThat(sut.id).isEqualTo(post.id)
        assertThat(sut.title).isEqualTo(post.titleValue)
        assertThat(sut.content).isEqualTo(post.contentValue)
        assertThat(sut.authorUsername).isEqualTo(post.authorUsername)
        assertThat(sut.createdAt).isEqualTo(post.createdAt)
        assertThat(sut.updatedAt).isEqualTo(post.updatedAt)
    }

    @Test
    @DisplayName("fromDomain - id null인 Post도 정상 변환된다 (저장 전 상태)")
    fun fromDomainWithNullId() {
        // given
        val post = post(id = null)

        // when
        val sut = PostEntity.fromDomain(post)

        // then
        assertThat(sut.id).isNull()
    }

    @Test
    @DisplayName("fromDomain -> toDomain 왕복 변환 시 모든 필드가 동일하다")
    fun roundTrip() {
        // given
        val post = post()

        // when
        val sut = PostEntity.fromDomain(post).toDomain()

        // then
        assertThat(sut.id).isEqualTo(post.id)
        assertThat(sut.titleValue).isEqualTo(post.titleValue)
        assertThat(sut.contentValue).isEqualTo(post.contentValue)
        assertThat(sut.authorUsername).isEqualTo(post.authorUsername)
        assertThat(sut.createdAt).isEqualTo(post.createdAt)
        assertThat(sut.updatedAt).isEqualTo(post.updatedAt)
    }
}
