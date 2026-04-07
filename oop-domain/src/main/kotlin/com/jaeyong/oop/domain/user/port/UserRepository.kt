package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User

interface UserRepository {
    fun save(user: User): User
    fun existsByUsername(username: String): Boolean
}
