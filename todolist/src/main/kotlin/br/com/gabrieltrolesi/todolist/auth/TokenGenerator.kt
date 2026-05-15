package br.com.gabrieltrolesi.todolist.auth

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.util.Base64

@Component
class TokenGenerator {
	private val secureRandom = SecureRandom()
	private val encoder = Base64.getUrlEncoder().withoutPadding()

	fun generate(): String {
		val bytes = ByteArray(32)
		secureRandom.nextBytes(bytes)
		return encoder.encodeToString(bytes)
	}
}
