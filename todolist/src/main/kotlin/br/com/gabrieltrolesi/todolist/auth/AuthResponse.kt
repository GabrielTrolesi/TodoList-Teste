package br.com.gabrieltrolesi.todolist.auth

import br.com.gabrieltrolesi.todolist.users.User
import br.com.gabrieltrolesi.todolist.users.UserResponse

data class AuthResponse(
	val tokenType: String = "Bearer",
	val accessToken: String,
	val user: UserResponse,
) {
	companion object {
		fun from(accessToken: String, user: User) = AuthResponse(
			accessToken = accessToken,
			user = UserResponse.from(user),
		)
	}
}
