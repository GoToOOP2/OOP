package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.result.LoginResult

interface LoginUseCase {

    /**
     * 사용자 인증 후 access/refresh token을 발급한다.
     *
     * @param command username, password를 담은 로그인 요청
     * @return access token과 refresh token
     * @throws com.jaeyong.oop.common.exception.BaseException 인증 실패 시 (UNAUTHORIZED)
     */
    fun login(command: LoginCommand): LoginResult
}
