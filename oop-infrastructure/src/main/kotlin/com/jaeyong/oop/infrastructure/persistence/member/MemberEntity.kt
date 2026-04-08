package com.jaeyong.oop.infrastructure.persistence.member

import com.jaeyong.oop.domain.member.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * 회원 JPA 엔티티.
 * DB의 members 테이블과 매핑된다.
 *
 * 도메인 모델(Member)과 JPA 엔티티를 분리하여
 * 도메인이 JPA에 의존하지 않도록 한다 (헥사고날 아키텍처).
 * toDomain() / fromDomain()으로 상호 변환한다.
 */
@Entity
@Table(name = "members")
class MemberEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    @Column(nullable = false, unique = true) // 이메일 중복 방지 (DB 레벨 유니크 제약)
    val email: String,
    @Column(nullable = false) // 해싱된 비밀번호가 저장됨
    val password: String,
    @Column(nullable = false, length = 20)
    val nickname: String,
) {
    /** JPA 엔티티 → 도메인 모델 변환 */
    fun toDomain(): Member = Member(id, email, password, nickname)

    companion object {
        /** 도메인 모델 → JPA 엔티티 변환 */
        fun fromDomain(member: Member): MemberEntity = MemberEntity(member.id, member.email, member.password, member.nickname)
    }
}
