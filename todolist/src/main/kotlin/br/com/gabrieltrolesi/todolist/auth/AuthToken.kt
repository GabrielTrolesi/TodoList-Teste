package br.com.gabrieltrolesi.todolist.auth

import br.com.gabrieltrolesi.todolist.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "auth_tokens")
class AuthToken(
	@Id
	@Column(nullable = false, updatable = false)
	var id: String = UUID.randomUUID().toString(),

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	var user: User,

	@Column(name = "token_hash", nullable = false, unique = true)
	var tokenHash: String,

	@Column(name = "created_at", nullable = false, updatable = false)
	var createdAt: Instant = Instant.now(),

	@Column(name = "expires_at", nullable = false)
	var expiresAt: Instant,

	@Column(nullable = false)
	var revoked: Boolean = false,
)
