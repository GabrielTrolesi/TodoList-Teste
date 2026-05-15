package br.com.gabrieltrolesi.todolist.auth

import br.com.gabrieltrolesi.todolist.shared.BusinessException
import br.com.gabrieltrolesi.todolist.shared.UnauthorizedException
import br.com.gabrieltrolesi.todolist.users.User
import br.com.gabrieltrolesi.todolist.users.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AuthServiceTest {
	private lateinit var userRepository: UserRepository
	private lateinit var authTokenRepository: AuthTokenRepository
	private lateinit var passwordEncoder: BCryptPasswordEncoder
	private lateinit var authService: AuthService

	@BeforeEach
	fun setUp() {
		userRepository = mock(UserRepository::class.java)
		authTokenRepository = mock(AuthTokenRepository::class.java)
		passwordEncoder = BCryptPasswordEncoder()
		authService = AuthService(
			userRepository = userRepository,
			authTokenRepository = authTokenRepository,
			passwordEncoder = passwordEncoder,
			tokenGenerator = TokenGenerator(),
			tokenHasher = TokenHasher(),
			tokenExpirationHours = 24,
		)

		`when`(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] }
		`when`(authTokenRepository.save(any(AuthToken::class.java))).thenAnswer { it.arguments[0] }
	}

	@Test
	fun `register creates user with encrypted password and token`() {
		`when`(userRepository.existsByEmail("ana@example.com")).thenReturn(false)

		val response = authService.register(
			RegisterRequest(name = "Ana", email = "ANA@example.com", password = "password123"),
		)

		val userCaptor = ArgumentCaptor.forClass(User::class.java)
		verify(userRepository).save(userCaptor.capture())

		assertEquals("ana@example.com", userCaptor.value.email)
		assertFalse(userCaptor.value.passwordHash == "password123")
		assertTrue(passwordEncoder.matches("password123", userCaptor.value.passwordHash))
		assertNotNull(response.accessToken)
		assertEquals("Bearer", response.tokenType)
		verify(authTokenRepository).save(any(AuthToken::class.java))
	}

	@Test
	fun `register rejects duplicated email`() {
		`when`(userRepository.existsByEmail("ana@example.com")).thenReturn(true)

		assertThrows(BusinessException::class.java) {
			authService.register(RegisterRequest(name = "Ana", email = "ana@example.com", password = "password123"))
		}

		verify(userRepository, never()).save(any(User::class.java))
		verify(authTokenRepository, never()).save(any(AuthToken::class.java))
	}

	@Test
	fun `login returns token for valid credentials`() {
		val user = User(
			name = "Ana",
			email = "ana@example.com",
			passwordHash = passwordEncoder.encode("password123")!!,
		)
		`when`(userRepository.findByEmail("ana@example.com")).thenReturn(user)

		val response = authService.login(LoginRequest(email = "ana@example.com", password = "password123"))

		assertNotNull(response.accessToken)
		assertEquals(user.email, response.user.email)
		verify(authTokenRepository).save(any(AuthToken::class.java))
	}

	@Test
	fun `login rejects invalid password`() {
		val user = User(
			name = "Ana",
			email = "ana@example.com",
			passwordHash = passwordEncoder.encode("password123")!!,
		)
		`when`(userRepository.findByEmail("ana@example.com")).thenReturn(user)

		assertThrows(UnauthorizedException::class.java) {
			authService.login(LoginRequest(email = "ana@example.com", password = "wrong-password"))
		}

		verify(authTokenRepository, never()).save(any(AuthToken::class.java))
	}
}
