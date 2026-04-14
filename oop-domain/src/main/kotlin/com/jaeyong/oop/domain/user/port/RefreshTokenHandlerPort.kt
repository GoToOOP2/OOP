package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface RefreshTokenHandlerPort {
    fun generateRefreshToken(username: UsernameVO): TokenVO
    fun validateAndExtractRefresh(token: TokenVO): UsernameVO?
}
