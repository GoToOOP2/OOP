package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.LoginCommand
import com.jaeyong.oop.application.user.result.LoginResult

interface LoginUseCase {
    fun login(command: LoginCommand): LoginResult
}
