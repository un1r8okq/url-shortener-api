package land.ver.url.shortener.dtos.urls

data class UrlResponseDTO(
    val longUrl: String,
    val shortenedUrl: String,
    val createdTimestampUtc: String,
    val lastVisitTimestampUtc: String?,
)
