package br.com.gabrieltrolesi.todolist.users

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant

class AuthenticatedUser(
	val id: String,
	val name: String,
	val createdAt: Instant,
	private val email: String,
	private val passwordHash: String,
) : UserDetails {
	override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
	override fun getPassword(): String = passwordHash
	override fun getUsername(): String = email
	override fun isAccountNonExpired(): Boolean = true
	override fun isAccountNonLocked(): Boolean = true
	override fun isCredentialsNonExpired(): Boolean = true
	override fun isEnabled(): Boolean = true
}
