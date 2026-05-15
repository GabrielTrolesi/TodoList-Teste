package br.com.gabrieltrolesi.todolist.todos

import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, String> {
	fun findAllByUserIdOrderByCreatedAtDesc(userId: String): List<Todo>
	fun findByIdAndUserId(id: String, userId: String): Todo?
}
