package br.com.gabrieltrolesi.todolist.shared

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException::class)
	fun handleBusinessException(exception: BusinessException): ResponseEntity<ApiError> {
		return buildError(HttpStatus.BAD_REQUEST, exception.message ?: "Requisicao invalida.")
	}

	@ExceptionHandler(UnauthorizedException::class)
	fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ApiError> {
		return buildError(HttpStatus.UNAUTHORIZED, exception.message ?: "Nao autorizado.")
	}

	@ExceptionHandler(NotFoundException::class)
	fun handleNotFoundException(exception: NotFoundException): ResponseEntity<ApiError> {
		return buildError(HttpStatus.NOT_FOUND, exception.message ?: "Recurso nao encontrado.")
	}

	@ExceptionHandler(MethodArgumentNotValidException::class)
	fun handleValidationException(exception: MethodArgumentNotValidException): ResponseEntity<ApiError> {
		val message = exception.bindingResult.fieldErrors.firstOrNull()?.defaultMessage ?: "Dados invalidos."
		return buildError(HttpStatus.BAD_REQUEST, message)
	}

	private fun buildError(status: HttpStatus, message: String): ResponseEntity<ApiError> {
		return ResponseEntity.status(status).body(ApiError(message = message, status = status.value()))
	}
}
