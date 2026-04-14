package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.JoinCommand

interface JoinUseCase {

    /**
     * 신규 사용자를 등록한다.
     *
     * @param command username, password를 담은 회원가입 요청
     * @throws com.jaeyong.oop.common.exception.BaseException username 중복 시 (DUPLICATE)
     */
    fun join(command: JoinCommand)
}
