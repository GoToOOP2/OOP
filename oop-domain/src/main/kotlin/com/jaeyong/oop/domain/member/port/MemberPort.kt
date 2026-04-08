package com.jaeyong.oop.domain.member.port

import com.jaeyong.oop.domain.member.Member

/**
 * 회원 저장소 아웃바운드 포트.
 * 도메인이 정의하고, infrastructure의 MemberPersistenceAdapter가 구현한다.
 * application 레이어(AuthService)가 이 포트를 통해 회원 데이터에 접근한다.
 */
interface MemberPort {
    /** 회원을 저장하고, 자동 생성된 id가 채워진 Member를 반환한다. */
    fun save(member: Member): Member

    /** 이메일로 회원을 조회한다. 없으면 null. */
    fun findByEmail(email: String): Member?

    /** 해당 이메일이 이미 등록되어 있는지 확인한다. 회원가입 시 중복 검사에 사용. */
    fun existsByEmail(email: String): Boolean
}
