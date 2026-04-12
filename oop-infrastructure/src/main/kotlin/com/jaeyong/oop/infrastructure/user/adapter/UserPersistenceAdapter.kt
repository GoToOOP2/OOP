package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.port.UserPort
import com.jaeyong.oop.infrastructure.user.entity.UserEntity
import com.jaeyong.oop.infrastructure.user.repository.UserEntityRepository
import org.springframework.stereotype.Repository

@Repository
class UserPersistenceAdapter(
    private val userEntityRepository: UserEntityRepository
) : UserPort {

    override fun register(user: User): User =
        userEntityRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun isUsernameTaken(username: String): Boolean =
        userEntityRepository.existsByUsername(username)

    override fun getByUsername(username: String): User? =
        userEntityRepository.findByUsername(username)?.toDomain()
}
