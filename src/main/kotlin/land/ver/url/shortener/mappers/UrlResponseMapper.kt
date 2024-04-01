package land.ver.url.shortener.mappers

import land.ver.url.shortener.SHORT_URL_PATH_PREFIX
import land.ver.url.shortener.dtos.urls.UrlResponse
import land.ver.url.shortener.models.Url
import java.time.Instant

class UrlResponseMapper {
    fun map(url: Url, lastVisitedTimeStamp: Instant?) = UrlResponse(
        longUrl = url.longUrl,
        shortenedUrl = System.getenv("SERVER_BASE_URL") + SHORT_URL_PATH_PREFIX + url.stub,
        createdTimestampUtc = url.createdTimestampUtc.toString(),
        lastVisitTimestampUtc = lastVisitedTimeStamp?.toString(),
    )
}
