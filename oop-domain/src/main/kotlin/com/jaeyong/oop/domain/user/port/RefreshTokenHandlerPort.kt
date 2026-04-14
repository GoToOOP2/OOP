package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface RefreshTokenHandlerPort {

    /**
     * Refresh token을 발급한다.
     *
     * @param username 토큰 subject로 사용할 사용자명
     * @return 서명된 refresh token
     */
    fun generateRefreshToken(username: UsernameVO): TokenVO

    /**
     * Refresh token을 검증하고 사용자명을 추출한다.
     *
     * @param token 검증할 refresh token
     * @return 유효한 refresh token이면 사용자명, 만료·서명 오류·access token이면 null
     */
    fun validateAndExtractRefresh(token: TokenVO): UsernameVO?
}
