package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface UserPort {
    fun register(user: User): User
    fun isUsernameTaken(username: UsernameVO): Boolean
    fun getByUsername(username: UsernameVO): User?
}
