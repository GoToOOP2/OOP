package com.jaeyong.oop.infrastructure.post.entity

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PostEntityTest {

    @Test
    fun `fromDomain으로 도메인을 엔티티로 변환한다`() {
        val post = Post.restore(1L, PostTitle.of("제목"), PostContent.of("내용"), "user1", LocalDateTime.now(), null)

        val entity = PostEntity.fromDomain(post)

        assertThat(entity.id).isEqualTo(1L)
        assertThat(entity.title).isEqualTo("제목")
        assertThat(entity.content).isEqualTo("내용")
        assertThat(entity.authorUsername).isEqualTo("user1")
    }

    @Test
    fun `toDomain으로 엔티티를 도메인으로 변환한다`() {
        val now = LocalDateTime.now()
        val entity = PostEntity(1L, "제목", "내용", "user1", now, null)

        val post = entity.toDomain()

        assertThat(post.id).isEqualTo(1L)
        assertThat(post.title.value).isEqualTo("제목")
        assertThat(post.content.value).isEqualTo("내용")
        assertThat(post.authorUsername).isEqualTo("user1")
    }
}
