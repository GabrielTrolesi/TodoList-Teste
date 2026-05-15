package br.com.gabrieltrolesi.todolist.auth

import br.com.gabrieltrolesi.todolist.users.AuthenticatedUser
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Instant

@Component
class BearerTokenAuthenticationFilter(
	private val authTokenRepository: AuthTokenRepository,
	private val tokenHasher: TokenHasher,
) : OncePerRequestFilter() {
	override fun doFilterInternal(
		request: HttpServletRequest,
		response: HttpServletResponse,
		filterChain: FilterChain,
	) {
		val token = request.getBearerToken()
		if (token != null && SecurityContextHolder.getContext().authentication == null) {
			authenticateToken(token)
		}

		filterChain.doFilter(request, response)
	}

	private fun authenticateToken(token: String) {
		val authToken = authTokenRepository.findByTokenHash(tokenHasher.hash(token))
			?.takeIf { !it.revoked && it.expiresAt.isAfter(Instant.now()) }
			?: return

		val principal = AuthenticatedUser(
			id = authToken.user.id,
			name = authToken.user.name,
			createdAt = authToken.user.createdAt,
			email = authToken.user.email,
			passwordHash = authToken.user.passwordHash,
		)
		val authentication = UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
		SecurityContextHolder.getContext().authentication = authentication
	}

	private fun HttpServletRequest.getBearerToken(): String? {
		val authorizationHeader = getHeader("Authorization") ?: return null
		return authorizationHeader
			.takeIf { it.startsWith("Bearer ", ignoreCase = true) }
			?.substringAfter("Bearer ")
			?.trim()
			?.takeIf { it.isNotBlank() }
	}
}
