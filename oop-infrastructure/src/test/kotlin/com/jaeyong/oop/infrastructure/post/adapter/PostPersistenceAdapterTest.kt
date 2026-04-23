package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.vo.ContentVO
import com.jaeyong.oop.domain.post.vo.TitleVO
import com.jaeyong.oop.infrastructure.post.jpa.PostJpaEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostPersistenceAdapterTest {

    @Mock
    private lateinit var postEntityRepository: PostEntityRepository

    @InjectMocks
    private lateinit var sut: PostPersistenceAdapter

    @Test
    @DisplayName("1. store 호출 시 저장된 도메인 Post를 반환한다")
    fun `store 호출 시 저장된 도메인 Post를 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val post = Post.create(TitleVO.from("제목"), ContentVO.from("내용"), authorId = 1L)
        val savedEntity = PostJpaEntity(id = 10L, title = "제목", content = "내용", authorId = 1L, deleted = false, createdAt = now, updatedAt = now)
        given(postEntityRepository.save(any())).willReturn(savedEntity)

        // when
        val result = sut.store(post)

        // then
        assertThat(result.id).isEqualTo(10L)
        assertThat(result.title).isEqualTo(TitleVO.from("제목"))
        assertThat(result.content).isEqualTo(ContentVO.from("내용"))
    }

    @Test
    @DisplayName("2. getActiveById - 존재하는 게시글이면 Post를 반환한다")
    fun `getActiveById - 존재하는 게시글이면 Post를 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val entity = PostJpaEntity(id = 1L, title = "제목", content = "내용", authorId = 1L, deleted = false, createdAt = now, updatedAt = now)
        given(postEntityRepository.getActiveByIdAndDeletedFalse(1L)).willReturn(entity)

        // when
        val result = sut.getActiveById(1L)

        // then
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1L)
    }

    @Test
    @DisplayName("3. getActiveById - 없는 게시글이면 null을 반환한다")
    fun `getActiveById - 없는 게시글이면 null을 반환한다`() {
        // given
        given(postEntityRepository.getActiveByIdAndDeletedFalse(999L)).willReturn(null)

        // when
        val result = sut.getActiveById(999L)

        // then
        assertThat(result).isNull()
    }

    @Test
    @DisplayName("4. getAllActive - 게시글이 있으면 List<Post>를 반환한다")
    fun `getAllActive - 게시글이 있으면 List Post 를 반환한다`() {
        // given
        val now = LocalDateTime.now()
        val entities = listOf(
            PostJpaEntity(id = 1L, title = "제목1", content = "내용1", authorId = 1L, deleted = false, createdAt = now, updatedAt = now),
            PostJpaEntity(id = 2L, title = "제목2", content = "내용2", authorId = 2L, deleted = false, createdAt = now, updatedAt = now)
        )
        given(postEntityRepository.getAllActiveByDeletedFalse()).willReturn(entities)

        // when
        val result = sut.getAllActiveActive()

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].id).isEqualTo(1L)
        assertThat(result[1].id).isEqualTo(2L)
    }

    @Test
    @DisplayName("5. getAllActive - 게시글이 없으면 빈 리스트를 반환한다")
    fun `getAllActive - 게시글이 없으면 빈 리스트를 반환한다`() {
        // given
        given(postEntityRepository.getAllActiveByDeletedFalse()).willReturn(emptyList())

        // when
        val result = sut.getAllActiveActive()

        // then
        assertThat(result).isEmpty()
    }

}
