package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User

interface UserPort {
    fun register(user: User): User
    fun isUsernameTaken(username: String): Boolean
    fun getByUsername(username: String): User?
}
