package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface JwtHandlerPort {

    /**
     * Access token을 발급한다.
     *
     * @param username 토큰 subject로 사용할 사용자명
     * @param userId 토큰 claim에 포함할 사용자 ID
     * @return 서명된 access token
     */
    fun generateToken(username: UsernameVO, userId: Long): TokenVO

    /**
     * Access token을 검증하고 사용자 ID를 추출한다.
     *
     * @param token 검증할 access token
     * @return 유효한 access token이면 사용자 ID, 만료·서명 오류·refresh token이면 null
     */
    fun validateAndExtract(token: TokenVO): Long?
}
