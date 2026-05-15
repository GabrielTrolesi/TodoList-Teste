package br.com.gabrieltrolesi.todolist.auth

import br.com.gabrieltrolesi.todolist.shared.BusinessException
import br.com.gabrieltrolesi.todolist.shared.UnauthorizedException
import br.com.gabrieltrolesi.todolist.users.User
import br.com.gabrieltrolesi.todolist.users.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthService(
	private val userRepository: UserRepository,
	private val authTokenRepository: AuthTokenRepository,
	private val passwordEncoder: PasswordEncoder,
	private val tokenGenerator: TokenGenerator,
	private val tokenHasher: TokenHasher,
	@Value("\${app.auth.token-expiration-hours}") private val tokenExpirationHours: Long,
) {
	@Transactional
	fun register(request: RegisterRequest): AuthResponse {
		val normalizedEmail = request.email.trim().lowercase()
		if (userRepository.existsByEmail(normalizedEmail)) {
			throw BusinessException("Email ja cadastrado.")
		}
		val passwordHash = passwordEncoder.encode(request.password)
			?: throw BusinessException("Nao foi possivel proteger a senha.")

		val user = userRepository.save(
			User(
				name = request.name.trim(),
				email = normalizedEmail,
				passwordHash = passwordHash,
			),
		)

		return issueToken(user)
	}

	@Transactional
	fun login(request: LoginRequest): AuthResponse {
		val normalizedEmail = request.email.trim().lowercase()
		val user = userRepository.findByEmail(normalizedEmail)
			?: throw UnauthorizedException("Email ou senha invalidos.")

		if (!passwordEncoder.matches(request.password, user.passwordHash)) {
			throw UnauthorizedException("Email ou senha invalidos.")
		}

		return issueToken(user)
	}

	private fun issueToken(user: User): AuthResponse {
		val token = tokenGenerator.generate()
		authTokenRepository.save(
			AuthToken(
				user = user,
				tokenHash = tokenHasher.hash(token),
				expiresAt = Instant.now().plus(tokenExpirationHours, ChronoUnit.HOURS),
			),
		)
		return AuthResponse.from(token, user)
	}
}
