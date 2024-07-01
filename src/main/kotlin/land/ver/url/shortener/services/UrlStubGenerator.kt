package land.ver.url.shortener.services

import land.ver.url.shortener.URL_STUB_LENGTH
import org.springframework.stereotype.Service

@Service
class UrlStubGenerator(private val rand: RandomNumberSource) {
    /**
     * Generate a short URL identifier.
     *
     * The short URL identifier uses the alphabet a-z, A-z, 0-9, "-" and "_"
     * which provides 26 + 26 + 10 + 1 + 1 = 64 characters. Short URL
     * identifiers are 4 characters long, which gives
     * 64^4 = 16,777,216 unique short codes
     */
    fun generate(): String {
        val allowedChars = ('a'..'z') + ('A'..'Z') + ('0'..'9') + ('-') + ('_')

        return (1..URL_STUB_LENGTH).joinToString("") {
            val index = rand.getRandomNumber(0, allowedChars.size)
            allowedChars[index].toString()
        }
    }
}
