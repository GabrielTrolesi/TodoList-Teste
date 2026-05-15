package br.com.gabrieltrolesi.todolist.config

import br.com.gabrieltrolesi.todolist.auth.BearerTokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class SecurityConfig {
	@Bean
	fun securityFilterChain(
		http: HttpSecurity,
		bearerTokenAuthenticationFilter: BearerTokenAuthenticationFilter,
	): SecurityFilterChain {
		return http
			.csrf { it.disable() }
			.cors { }
			.httpBasic { it.disable() }
			.formLogin { it.disable() }
			.logout { it.disable() }
			.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
			.authorizeHttpRequests {
				it.requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
				it.requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
				it.anyRequest().authenticated()
			}
			.addFilterBefore(bearerTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
			.build()
	}

	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun userDetailsService(): UserDetailsService {
		return UserDetailsService { throw UsernameNotFoundException("Autenticacao por usuario e senha nao e usada.") }
	}

	@Bean
	fun corsConfigurationSource(): CorsConfigurationSource {
		val configuration = CorsConfiguration().apply {
			allowedOrigins = listOf(
				"http://127.0.0.1:4321",
				"http://127.0.0.1:4322",
				"http://localhost:4321",
				"http://localhost:4322",
			)
			allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
			allowedHeaders = listOf("Authorization", "Content-Type")
			exposedHeaders = listOf("Authorization")
			allowCredentials = false
		}

		return UrlBasedCorsConfigurationSource().apply {
			registerCorsConfiguration("/**", configuration)
		}
	}

	@Bean
	fun webMvcConfigurer(): WebMvcConfigurer {
		return object : WebMvcConfigurer {
			override fun addCorsMappings(registry: CorsRegistry) {
				registry.addMapping("/**")
					.allowedOrigins(
						"http://127.0.0.1:4321",
						"http://127.0.0.1:4322",
						"http://localhost:4321",
						"http://localhost:4322",
					)
					.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
					.allowedHeaders("Authorization", "Content-Type")
			}
		}
	}
}
