package land.ver.url.shortener.services

import org.springframework.stereotype.Service

private const val MAX_LENGTH = 64

@Service
class RandomStringGenerator(private val rand: RandomNumberSource) {
    /**
     * Generate a random string
     *
     * This method uses the alphabet a-z, A-z, 0-9, "-" and "_"
     * which provides 26 + 26 + 10 + 1 + 1 = 64 characters. If a length of 4
     * is used, you get 64^4 = 16,777,216 unique strings
     */
    fun generate(length: Int): String {
        val alphabet = ('a'..'z') + ('A'..'Z') + ('0'..'9') + ('-') + ('_')

        return generate(alphabet, length)
    }

    fun generate(alphabet: List<Char>, length: Int): String {
        require(length in 0..MAX_LENGTH) { "Length must be 0 <= n <= $MAX_LENGTH" }

        return (1..length).joinToString(separator = "") {
            val index = rand.getRandomNumber(0, alphabet.size)
            alphabet[index].toString()
        }
    }
}
