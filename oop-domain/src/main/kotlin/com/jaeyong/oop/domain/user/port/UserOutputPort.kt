package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User

interface UserOutputPort {
    fun save(user: User): User
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): User?
}
