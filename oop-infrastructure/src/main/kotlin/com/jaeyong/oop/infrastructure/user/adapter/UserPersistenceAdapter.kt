package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO
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

    override fun isUsernameTaken(username: UsernameVO): Boolean =
        userEntityRepository.existsByUsername(username.value)

    override fun getByUsername(username: UsernameVO): User? =
        userEntityRepository.findByUsername(username.value)?.toDomain()
}
