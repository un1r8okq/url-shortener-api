package land.ver.url.shortener.models

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant
import java.util.*

@Entity
@Table(name = "url_visits")
data class UrlVisit(
    @Id
    val id: UUID,
    val urlId: UUID,
    val timestampUtc: Instant,
)
