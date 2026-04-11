package com.jaeyong.oop.infrastructure.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long>, UserRepository {
    override fun existsByUsername(username: String): Boolean
    override fun findByUsername(username: String): UserEntity?
}
