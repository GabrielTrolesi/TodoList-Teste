package br.com.gabrieltrolesi.todolist.todos

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
@Table(name = "todos")
class Todo(
	@Id
	@Column(nullable = false, updatable = false)
	var id: String = UUID.randomUUID().toString(),

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	var user: User,

	@Column(nullable = false)
	var title: String,

	@Column
	var description: String? = null,

	@Column(nullable = false)
	var completed: Boolean = false,

	@Column(name = "created_at", nullable = false, updatable = false)
	var createdAt: Instant = Instant.now(),

	@Column(name = "updated_at", nullable = false)
	var updatedAt: Instant = Instant.now(),

	@Column(name = "completed_at")
	var completedAt: Instant? = null,
)
