package br.com.gabrieltrolesi.todolist.todos

import br.com.gabrieltrolesi.todolist.shared.NotFoundException
import br.com.gabrieltrolesi.todolist.users.User
import br.com.gabrieltrolesi.todolist.users.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.any
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class TodoServiceTest {
	private lateinit var todoRepository: TodoRepository
	private lateinit var userRepository: UserRepository
	private lateinit var todoService: TodoService
	private lateinit var user: User

	@BeforeEach
	fun setUp() {
		todoRepository = mock(TodoRepository::class.java)
		userRepository = mock(UserRepository::class.java)
		todoService = TodoService(todoRepository, userRepository)
		user = User(
			id = "user-1",
			name = "Ana",
			email = "ana@example.com",
			passwordHash = "hash",
		)

		`when`(todoRepository.save(any(Todo::class.java))).thenAnswer { it.arguments[0] }
	}

	@Test
	fun `create associates todo with authenticated user`() {
		`when`(userRepository.getReferenceById(user.id)).thenReturn(user)

		val response = todoService.create(
			user.id,
			CreateTodoRequest(title = " Comprar pao ", description = " Ir na padaria "),
		)

		val todoCaptor = ArgumentCaptor.forClass(Todo::class.java)
		verify(todoRepository).save(todoCaptor.capture())

		assertEquals(user.id, todoCaptor.value.user.id)
		assertEquals("Comprar pao", todoCaptor.value.title)
		assertEquals("Ir na padaria", todoCaptor.value.description)
		assertEquals("Comprar pao", response.title)
		assertFalseOrThrow(response.completed)
	}

	@Test
	fun `update edits only owned todo`() {
		val todo = Todo(
			id = "todo-1",
			user = user,
			title = "Antigo",
			description = "Descricao antiga",
		)
		`when`(todoRepository.findByIdAndUserId("todo-1", user.id)).thenReturn(todo)

		val response = todoService.update(
			user.id,
			"todo-1",
			UpdateTodoRequest(title = "Novo titulo", description = "Nova descricao"),
		)

		assertEquals("Novo titulo", response.title)
		assertEquals("Nova descricao", response.description)
		assertNotNull(response.updatedAt)
		verify(todoRepository).save(todo)
	}

	@Test
	fun `complete marks todo as completed and timestamps it`() {
		val todo = Todo(
			id = "todo-2",
			user = user,
			title = "Finalizar tarefa",
		)
		`when`(todoRepository.findByIdAndUserId("todo-2", user.id)).thenReturn(todo)

		val response = todoService.complete(user.id, "todo-2")

		assertTrue(response.completed)
		assertNotNull(response.completedAt)
		verify(todoRepository).save(todo)
	}

	@Test
	fun `delete removes owned todo`() {
		val todo = Todo(
			id = "todo-3",
			user = user,
			title = "Excluir tarefa",
		)
		`when`(todoRepository.findByIdAndUserId("todo-3", user.id)).thenReturn(todo)
		doNothing().`when`(todoRepository).delete(todo)

		todoService.delete(user.id, "todo-3")

		verify(todoRepository).delete(todo)
	}

	@Test
	fun `find by id rejects access to another users todo`() {
		`when`(todoRepository.findByIdAndUserId("todo-4", user.id)).thenReturn(null)

		assertThrows(NotFoundException::class.java) {
			todoService.findById(user.id, "todo-4")
		}

		verify(todoRepository, never()).save(any(Todo::class.java))
	}

	@Test
	fun `update clears blank description`() {
		val todo = Todo(
			id = "todo-5",
			user = user,
			title = "Titulo",
			description = "Descricao",
		)
		`when`(todoRepository.findByIdAndUserId("todo-5", user.id)).thenReturn(todo)

		val response = todoService.update(
			user.id,
			"todo-5",
			UpdateTodoRequest(title = "Titulo ajustado", description = "   "),
		)

		assertNull(response.description)
	}

	private fun assertFalseOrThrow(value: Boolean) {
		if (value) {
			throw AssertionError("Expected false but was true")
		}
	}
}
