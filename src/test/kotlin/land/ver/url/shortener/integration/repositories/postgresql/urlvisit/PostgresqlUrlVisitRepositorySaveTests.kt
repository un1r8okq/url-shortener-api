package land.ver.url.shortener.integration.repositories.postgresql.urlvisit

import com.github.f4b6a3.uuid.UuidCreator
import jakarta.persistence.EntityManager
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.repositories.postgresql.PostgresqlUrlVisitRepository
import land.ver.url.shortener.repositories.postgresql.models.Url
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Instant

class PostgresqlUrlVisitRepositorySaveTests(
    @Autowired private val repository: PostgresqlUrlVisitRepository,
    @Autowired private val entityManager: EntityManager,
) : BaseRepositoryTest() {
    @Test
    fun `the ID is generated correctly`() {
        val url = Url(
            id = UuidCreator.getTimeOrderedEpoch(),
            longUrl = "https://example.com/?q=something",
            stub = "stub",
            createdTimestampUtc = Instant.EPOCH,
        )
        entityManager.persist(url)
        entityManager.flush()

        val result = repository.save(url.id)

        assertNotNull(result.id)
    }
}
