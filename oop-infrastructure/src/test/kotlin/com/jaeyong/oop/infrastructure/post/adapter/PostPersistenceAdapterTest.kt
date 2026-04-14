package com.jaeyong.oop.infrastructure.post.adapter

import com.jaeyong.oop.domain.post.Post
import com.jaeyong.oop.domain.post.PostContent
import com.jaeyong.oop.domain.post.PostTitle
import com.jaeyong.oop.infrastructure.post.entity.PostEntity
import com.jaeyong.oop.infrastructure.post.repository.PostEntityRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.then
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.springframework.data.domain.PageRequest
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PostPersistenceAdapterTest {

    @Mock private lateinit var postEntityRepository: PostEntityRepository
    @InjectMocks private lateinit var sut: PostPersistenceAdapter

    private val now = LocalDateTime.now()

    @Test
    fun `save 호출 시 저장된 도메인 객체를 반환한다`() {
        val post = Post.restore(null, PostTitle.of("제목"), PostContent.of("내용"), "user1", now, null)
        val savedEntity = PostEntity(1L, "제목", "내용", "user1", now, null)
        given(postEntityRepository.save(any())).willReturn(savedEntity)

        val result = sut.save(post)

        assertThat(result.id).isEqualTo(1L)
        assertThat(result.title.value).isEqualTo("제목")
    }

    @Test
    fun `findById 호출 시 존재하면 도메인 객체를 반환한다`() {
        val entity = PostEntity(1L, "제목", "내용", "user1", now, null)
        given(postEntityRepository.findPostById(1L)).willReturn(entity)

        val result = sut.findById(1L)

        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(1L)
    }

    @Test
    fun `findById 호출 시 존재하지 않으면 null을 반환한다`() {
        given(postEntityRepository.findPostById(999L)).willReturn(null)

        val result = sut.findById(999L)

        assertThat(result).isNull()
    }

    @Test
    fun `findAll 호출 시 페이지네이션된 목록을 반환한다`() {
        val entities = listOf(
            PostEntity(1L, "제목1", "내용1", "user1", now, null),
            PostEntity(2L, "제목2", "내용2", "user2", now, null)
        )
        given(postEntityRepository.findAllPaged(PageRequest.of(0, 10))).willReturn(entities)

        val result = sut.findAll(0, 10)

        assertThat(result).hasSize(2)
    }

    @Test
    fun `countAll 호출 시 전체 게시글 수를 반환한다`() {
        given(postEntityRepository.count()).willReturn(5L)

        val result = sut.countAll()

        assertThat(result).isEqualTo(5L)
    }

    @Test
    fun `deleteById 호출 시 삭제 메서드를 호출한다`() {
        sut.deleteById(1L)

        then(postEntityRepository).should().deleteById(1L)
    }
}
