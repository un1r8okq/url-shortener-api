package land.ver.url.shortener.models

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "url_visits")
data class UrlVisit(
    @Id
    val id: UUID,
    val timestampUtc: Instant,
    @ManyToOne
    @JoinColumn(name = "url_id")
    val url: Url?,
) {
    constructor() : this(UuidCreator.getTimeBased(), Instant.now(), null)
}
