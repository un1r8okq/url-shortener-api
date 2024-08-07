package land.ver.url.shortener

import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity) =
        http
            .csrf { }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/status").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {}
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint { request, response, authException ->
                    // Return Forbidden instead of redirecting to OAuth login for unauthenticated sessions
                    response.sendError(HttpServletResponse.SC_FORBIDDEN)
                }
            }
            .build()
}
