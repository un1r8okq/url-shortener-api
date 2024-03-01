package land.ver.url_shortener.dtos

import jakarta.validation.constraints.NotEmpty
import org.hibernate.validator.constraints.URL

data class ShortenUrlRequest(
    @field:URL
    @field:NotEmpty
    val longUrl: String
)
