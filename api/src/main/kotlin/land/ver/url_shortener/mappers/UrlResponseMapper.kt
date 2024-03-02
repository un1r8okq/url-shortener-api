package land.ver.url_shortener.mappers

import land.ver.url_shortener.SHORT_URL_PATH_PREFIX
import land.ver.url_shortener.dtos.urls.UrlResponse
import land.ver.url_shortener.models.Url

class UrlResponseMapper {
    fun map(url: Url) = UrlResponse(
        longUrl = url.longUrl,
        shortenedUrl = System.getenv("SERVER_BASE_URL") + SHORT_URL_PATH_PREFIX,
    )
}