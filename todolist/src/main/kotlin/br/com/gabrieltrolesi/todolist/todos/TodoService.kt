package br.com.gabrieltrolesi.todolist.todos

import br.com.gabrieltrolesi.todolist.shared.NotFoundException
import br.com.gabrieltrolesi.todolist.users.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class TodoService(
	private val todoRepository: TodoRepository,
	private val userRepository: UserRepository,
) {
	@Transactional(readOnly = true)
	fun listByUserId(userId: String): List<TodoResponse> {
		return todoRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
			.map(TodoResponse::from)
	}

	@Transactional(readOnly = true)
	fun findById(userId: String, todoId: String): TodoResponse {
		return TodoResponse.from(findOwnedTodo(userId, todoId))
	}

	@Transactional
	fun create(userId: String, request: CreateTodoRequest): TodoResponse {
		val now = Instant.now()
		val todo = todoRepository.save(
			Todo(
				user = userRepository.getReferenceById(userId),
				title = request.title.trim(),
				description = request.description?.trim()?.takeIf { it.isNotBlank() },
				createdAt = now,
				updatedAt = now,
			),
		)
		return TodoResponse.from(todo)
	}

	@Transactional
	fun update(userId: String, todoId: String, request: UpdateTodoRequest): TodoResponse {
		val todo = findOwnedTodo(userId, todoId)
		todo.title = request.title.trim()
		todo.description = request.description?.trim()?.takeIf { it.isNotBlank() }
		todo.updatedAt = Instant.now()
		return TodoResponse.from(todoRepository.save(todo))
	}

	@Transactional
	fun complete(userId: String, todoId: String): TodoResponse {
		val todo = findOwnedTodo(userId, todoId)
		val now = Instant.now()
		todo.completed = true
		todo.completedAt = todo.completedAt ?: now
		todo.updatedAt = now
		return TodoResponse.from(todoRepository.save(todo))
	}

	@Transactional
	fun delete(userId: String, todoId: String) {
		val todo = findOwnedTodo(userId, todoId)
		todoRepository.delete(todo)
	}

	private fun findOwnedTodo(userId: String, todoId: String): Todo {
		return todoRepository.findByIdAndUserId(todoId, userId)
			?: throw NotFoundException("Tarefa nao encontrada.")
	}
}
