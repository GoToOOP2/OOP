package com.jaeyong.oop.infrastructure.user

interface UserRepository {
    fun save(entity: UserEntity): UserEntity
    fun existsByUsername(username: String): Boolean
    fun findByUsername(username: String): UserEntity?
}
