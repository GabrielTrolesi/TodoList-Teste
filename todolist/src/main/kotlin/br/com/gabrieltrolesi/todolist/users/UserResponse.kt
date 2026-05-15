package br.com.gabrieltrolesi.todolist.users

import java.time.Instant

data class UserResponse(
	val id: String,
	val name: String,
	val email: String,
	val createdAt: Instant,
) {
	companion object {
		fun from(user: User) = UserResponse(
			id = user.id,
			name = user.name,
			email = user.email,
			createdAt = user.createdAt,
		)
	}
}
