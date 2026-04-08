package com.jaeyong.oop.infrastructure.persistence.member

import com.jaeyong.oop.domain.member.Member
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
@Import(MemberPersistenceAdapter::class)
class MemberPersistenceAdapterTest {
    @Autowired
    private lateinit var adapter: MemberPersistenceAdapter

    @Test
    @DisplayName("회원을 저장하면 id가 자동 생성되어야 한다")
    fun `회원을 저장하면 id가 자동 생성된다`() {
        // given
        val member = Member(email = "test@example.com", password = "hashed", nickname = "닉네임")

        // when
        val saved = adapter.save(member)

        // then
        assertThat(saved.id).isNotNull()
        assertThat(saved.email).isEqualTo("test@example.com")
        assertThat(saved.password).isEqualTo("hashed")
        assertThat(saved.nickname).isEqualTo("닉네임")
    }

    @Test
    @DisplayName("이메일로 회원을 조회할 수 있어야 한다")
    fun `이메일로 회원을 조회할 수 있다`() {
        // given
        adapter.save(Member(email = "test@example.com", password = "hashed", nickname = "닉네임"))

        // when
        val found = adapter.findByEmail("test@example.com")

        // then
        assertThat(found).isNotNull
        assertThat(found!!.email).isEqualTo("test@example.com")
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 조회하면 null을 반환해야 한다")
    fun `존재하지 않는 이메일로 조회하면 null을 반환한다`() {
        // when
        val found = adapter.findByEmail("notfound@example.com")

        // then
        assertThat(found).isNull()
    }

    @Test
    @DisplayName("이미 등록된 이메일은 existsByEmail이 true를 반환해야 한다")
    fun `이미 등록된 이메일은 existsByEmail이 true를 반환한다`() {
        // given
        adapter.save(Member(email = "test@example.com", password = "hashed", nickname = "닉네임"))

        // when & then
        assertThat(adapter.existsByEmail("test@example.com")).isTrue()
    }

    @Test
    @DisplayName("등록되지 않은 이메일은 existsByEmail이 false를 반환해야 한다")
    fun `등록되지 않은 이메일은 existsByEmail이 false를 반환한다`() {
        // when & then
        assertThat(adapter.existsByEmail("notfound@example.com")).isFalse()
    }
}
