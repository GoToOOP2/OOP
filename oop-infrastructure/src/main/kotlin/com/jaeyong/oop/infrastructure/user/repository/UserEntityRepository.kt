package com.jaeyong.oop.infrastructure.user.repository

import com.jaeyong.oop.infrastructure.user.entity.UserEntity

interface UserEntityRepository {
    fun save(entity: UserEntity): UserEntity
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserEntity?
}
