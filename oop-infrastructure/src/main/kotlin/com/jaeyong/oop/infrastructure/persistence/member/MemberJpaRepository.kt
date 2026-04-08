package com.jaeyong.oop.infrastructure.persistence.member

import org.springframework.data.jpa.repository.JpaRepository

/**
 * 회원 JPA Repository.
// * Spring Data JPA가 인터페이스 -> 메서드 이름을 분석하여 자동으로 쿼리를 생성한다.
 * MemberPersistenceAdapter에서만 사용되며, 외부 레이어에 직접 노출되지 않는다.
 */
interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?

    fun existsByEmail(email: String): Boolean
}
