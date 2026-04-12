package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.common.JoinCommand

interface JoinUseCase {
    fun join(command: JoinCommand)
}
