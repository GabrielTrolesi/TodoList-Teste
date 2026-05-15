package br.com.gabrieltrolesi.todolist.auth

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AuthTokenRepository : JpaRepository<AuthToken, String> {
	@Query("select authToken from AuthToken authToken join fetch authToken.user where authToken.tokenHash = :tokenHash")
	fun findByTokenHash(tokenHash: String): AuthToken?
}
