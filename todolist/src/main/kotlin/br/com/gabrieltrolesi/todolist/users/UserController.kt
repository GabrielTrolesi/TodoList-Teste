package br.com.gabrieltrolesi.todolist.users

import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController {
	@GetMapping("/me")
	fun currentUser(@AuthenticationPrincipal authenticatedUser: AuthenticatedUser): UserResponse {
		return UserResponse(
			id = authenticatedUser.id,
			name = authenticatedUser.name,
			email = authenticatedUser.username,
			createdAt = authenticatedUser.createdAt,
		)
	}
}
