package br.com.gabrieltrolesi.todolist.users

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
	fun existsByEmail(email: String): Boolean
	fun findByEmail(email: String): User?
}
