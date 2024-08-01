package land.ver.url.shortener

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
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/v1/auth/status").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login {}
            .build()
}
