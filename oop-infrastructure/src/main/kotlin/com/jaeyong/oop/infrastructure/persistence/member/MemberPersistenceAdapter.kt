package com.jaeyong.oop.infrastructure.persistence.member

import com.jaeyong.oop.domain.member.Member
import com.jaeyong.oop.domain.member.port.MemberPort
import org.springframework.stereotype.Repository

/**
 * MemberPort의 구현체 (아웃바운드 어댑터).
 * 도메인 모델(Member) ↔ JPA 엔티티(MemberEntity) 변환을 처리하고,
 * Spring Data JPA를 통해 실제 DB 작업을 수행한다.
 */
@Repository
class MemberPersistenceAdapter(private val memberJpaRepository: MemberJpaRepository, ) : MemberPort {

    override fun save(member: Member): Member {
        val entity = MemberEntity.fromDomain(member) // 도메인 → 엔티티 변환
        return memberJpaRepository.save(entity).toDomain() // 저장 후 엔티티 → 도메인 변환 (id 채워짐)
    }

    override fun findByEmail(email: String): Member? {
        return memberJpaRepository.findByEmail(email)?.toDomain()
    }

    override fun existsByEmail(email: String): Boolean {
        return memberJpaRepository.existsByEmail(email)
    }
}
