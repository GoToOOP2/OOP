package com.jaeyong.oop.infrastructure.user.repository

import com.jaeyong.oop.infrastructure.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long>, UserEntityRepository {
    override fun existsByUsername(username: String): Boolean
    override fun findByUsername(username: String): UserEntity?
}
