package com.jaeyong.oop.domain.port

/**
 * Refresh Token 저장소 아웃바운드 포트.
 * 도메인이 정의하고, infrastructure의 RefreshTokenPersistenceAdapter가 구현한다.
 * Refresh Token은 항상 해싱된 상태로 저장된다 (평문 저장 안 함).
 */
interface RefreshTokenPort {
    /**
     * 회원의 Refresh Token(해싱됨)을 저장한다.
     * 이미 존재하면 갱신, 없으면 새로 생성한다. (토큰 로테이션)
     */
    fun save(memberId: Long, hashedToken: String, )

    /** 회원의 저장된 해싱 Refresh Token을 조회한다. 없으면 null. */
    fun findByMemberId(memberId: Long): String?

    /** 회원의 Refresh Token을 삭제한다. 로그아웃 시 호출. */
    fun deleteByMemberId(memberId: Long)
}
