package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.vo.TokenVO
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface JwtHandlerPort {
    fun generateToken(username: UsernameVO): TokenVO
    fun validateAndExtract(token: TokenVO): UsernameVO?
}
