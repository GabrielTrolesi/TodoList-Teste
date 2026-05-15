package br.com.gabrieltrolesi.todolist.todos

import br.com.gabrieltrolesi.todolist.users.AuthenticatedUser
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.annotation.AuthenticationPrincipal

@RestController
@RequestMapping("/api/todos")
class TodoController(
	private val todoService: TodoService,
) {
	@GetMapping
	fun list(@AuthenticationPrincipal authenticatedUser: AuthenticatedUser): List<TodoResponse> {
		return todoService.listByUserId(authenticatedUser.id)
	}

	@GetMapping("/{todoId}")
	fun findById(
		@PathVariable todoId: String,
		@AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
	): TodoResponse {
		return todoService.findById(authenticatedUser.id, todoId)
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	fun create(
		@Valid @RequestBody request: CreateTodoRequest,
		@AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
	): TodoResponse {
		return todoService.create(authenticatedUser.id, request)
	}

	@PutMapping("/{todoId}")
	fun update(
		@PathVariable todoId: String,
		@Valid @RequestBody request: UpdateTodoRequest,
		@AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
	): TodoResponse {
		return todoService.update(authenticatedUser.id, todoId, request)
	}

	@PatchMapping("/{todoId}/complete")
	fun complete(
		@PathVariable todoId: String,
		@AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
	): TodoResponse {
		return todoService.complete(authenticatedUser.id, todoId)
	}

	@DeleteMapping("/{todoId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	fun delete(
		@PathVariable todoId: String,
		@AuthenticationPrincipal authenticatedUser: AuthenticatedUser,
	) {
		todoService.delete(authenticatedUser.id, todoId)
	}
}
