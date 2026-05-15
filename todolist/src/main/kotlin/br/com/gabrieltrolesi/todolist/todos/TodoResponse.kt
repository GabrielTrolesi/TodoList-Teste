package br.com.gabrieltrolesi.todolist.todos

import java.time.Instant

data class TodoResponse(
	val id: String,
	val title: String,
	val description: String?,
	val completed: Boolean,
	val createdAt: Instant,
	val updatedAt: Instant,
	val completedAt: Instant?,
) {
	companion object {
		fun from(todo: Todo) = TodoResponse(
			id = todo.id,
			title = todo.title,
			description = todo.description,
			completed = todo.completed,
			createdAt = todo.createdAt,
			updatedAt = todo.updatedAt,
			completedAt = todo.completedAt,
		)
	}
}
