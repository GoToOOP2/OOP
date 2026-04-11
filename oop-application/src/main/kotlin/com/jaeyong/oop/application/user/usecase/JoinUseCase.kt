package com.jaeyong.oop.application.user.usecase

import com.jaeyong.oop.application.user.service.JoinCommand

interface JoinUseCase {
    fun join(command: JoinCommand)
}
