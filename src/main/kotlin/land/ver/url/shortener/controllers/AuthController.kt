package land.ver.url.shortener.controllers

import land.ver.url.shortener.dtos.UserResult
import land.ver.url.shortener.dtos.auth.CsrfTokenResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    @GetMapping("user")
    fun getUser(@AuthenticationPrincipal principal: OidcUser?): ResponseEntity<UserResult?> {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity.ok(
            UserResult(
                name = principal.fullName,
                email = principal.email,
            ),
        )
    }

    @GetMapping("csrf-token")
    fun getCsrfToken(csrfToken: CsrfToken): ResponseEntity<CsrfTokenResponse> {
        return ResponseEntity.ok(CsrfTokenResponse(csrfToken.token))
    }
}
