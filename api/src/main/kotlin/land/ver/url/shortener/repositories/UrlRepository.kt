package land.ver.url.shortener.repositories

import land.ver.url.shortener.models.Url
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UrlRepository : JpaRepository<Url, UUID> {
    fun findByStub(stub: String): Url?
}
