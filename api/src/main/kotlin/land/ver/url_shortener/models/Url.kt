package land.ver.url_shortener.models

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.GenericGenerator
import java.time.Clock
import java.time.Instant
import java.util.*

@Entity
@Table(name = "urls")
data class Url(
    @Id
    @GeneratedValue(generator = "uuidv7")
    @GenericGenerator(name = "uuidv7", strategy = "land.ver.url_shortener.Uuidv7Generator")
    val id: UUID?,
    val longUrl: String,
    val stub: String,
    val createdTimestampUtc: Instant,
) {
    constructor(): this(null, "", "", Instant.now(Clock.systemUTC()))
}
