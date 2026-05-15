package br.com.gabrieltrolesi.todolist.todos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateTodoRequest(
	@field:NotBlank
	@field:Size(max = 120)
	val title: String,

	@field:Size(max = 1000)
	val description: String? = null,
)

data class UpdateTodoRequest(
	@field:NotBlank
	@field:Size(max = 120)
	val title: String,

	@field:Size(max = 1000)
	val description: String? = null,
)
