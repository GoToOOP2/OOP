package com.jaeyong.oop.common.auth

object AuthContext {
    private val holder = ThreadLocal<String?>()

    fun set(username: String) = holder.set(username)
    fun get(): String? = holder.get()
    fun clear() = holder.remove()
}
