package land.ver.url.shortener.repositories

import land.ver.url.shortener.models.UrlVisit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UrlVisitsRepository : JpaRepository<UrlVisit, UUID> {
    fun findAllByUrlIds(urlIds: List<UUID>): List<UrlVisit>
}
