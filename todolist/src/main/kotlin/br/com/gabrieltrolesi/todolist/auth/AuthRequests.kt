package br.com.gabrieltrolesi.todolist.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
	@field:NotBlank
	val name: String,

	@field:Email
	@field:NotBlank
	val email: String,

	@field:Size(min = 8)
	val password: String,
)

data class LoginRequest(
	@field:Email
	@field:NotBlank
	val email: String,

	@field:NotBlank
	val password: String,
)
