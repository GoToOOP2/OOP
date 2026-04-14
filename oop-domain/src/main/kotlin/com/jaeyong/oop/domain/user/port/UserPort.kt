package com.jaeyong.oop.domain.user.port

import com.jaeyong.oop.domain.user.User
import com.jaeyong.oop.domain.user.vo.UsernameVO

interface UserPort {

    /**
     * 사용자를 저장하고 저장된 사용자를 반환한다.
     *
     * @param user 저장할 사용자
     * @return 저장된 사용자
     */
    fun register(user: User): User

    /**
     * username 중복 여부를 확인한다.
     *
     * @param username 확인할 사용자명
     * @return 이미 사용 중이면 true
     */
    fun isUsernameTaken(username: UsernameVO): Boolean

    /**
     * username으로 사용자를 조회한다.
     *
     * @param username 조회할 사용자명
     * @return 사용자가 존재하면 User, 없으면 null
     */
    fun getByUsername(username: UsernameVO): User?
}
