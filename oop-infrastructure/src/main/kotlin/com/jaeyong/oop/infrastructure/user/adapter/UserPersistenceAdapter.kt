package com.jaeyong.oop.infrastructure.user.adapter

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO
import com.jaeyong.oop.domain.user.port.UserPort
import com.jaeyong.oop.domain.user.port.UserQueryPort
import com.jaeyong.oop.infrastructure.user.entity.UserEntity
import com.jaeyong.oop.infrastructure.user.repository.UserEntityRepository
import org.springframework.stereotype.Repository

/**
 * [UserPort] Outbound Adapter — JPA를 통해 User를 저장하고 조회한다.
 */
@Repository
class UserPersistenceAdapter(
    private val userEntityRepository: UserEntityRepository
) : UserPort, UserQueryPort {

    override fun register(user: User): User =
        userEntityRepository.save(UserEntity.fromDomain(user)).toDomain()

    override fun isUsernameTaken(username: UsernameVO): Boolean =
        userEntityRepository.existsByUsername(username.value)

    override fun getByUsername(username: UsernameVO): User? =
        userEntityRepository.findByUsername(username.value)?.toDomain()

    override fun findById(id: Long): User? =
        userEntityRepository.findUserById(id)?.toDomain()

    override fun findByIds(ids: List<Long>): Map<Long, User> =
        userEntityRepository.findAllByIdIn(ids)
            .associate { it.id!! to it.toDomain() }
}
