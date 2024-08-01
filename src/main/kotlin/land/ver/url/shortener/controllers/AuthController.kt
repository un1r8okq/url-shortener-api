package land.ver.url.shortener.controllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {
    @GetMapping("status")
    fun getStatus(@AuthenticationPrincipal principal: OidcUser?): ResponseEntity<Any> {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        return ResponseEntity
            .ok()
            .body(mapOf("name" to principal.name))
    }
}
