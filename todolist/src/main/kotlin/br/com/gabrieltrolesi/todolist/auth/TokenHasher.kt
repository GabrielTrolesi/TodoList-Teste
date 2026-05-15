package br.com.gabrieltrolesi.todolist.auth

import org.springframework.stereotype.Component
import java.security.MessageDigest

@Component
class TokenHasher {
	fun hash(token: String): String {
		val digest = MessageDigest.getInstance("SHA-256").digest(token.toByteArray())
		return digest.joinToString(separator = "") { "%02x".format(it) }
	}
}
