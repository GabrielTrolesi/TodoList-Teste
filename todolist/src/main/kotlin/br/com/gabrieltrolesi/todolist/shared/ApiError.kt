package br.com.gabrieltrolesi.todolist.shared

import java.time.Instant

data class ApiError(
	val message: String,
	val status: Int,
	val timestamp: Instant = Instant.now(),
)
