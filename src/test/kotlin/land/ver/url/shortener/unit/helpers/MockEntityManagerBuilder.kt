package land.ver.url.shortener.unit.helpers

import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import jakarta.persistence.Parameter
import jakarta.persistence.Query
import land.ver.url.shortener.models.Url

class MockEntityManagerBuilder {
    lateinit var query: Query
    private var singleResult: Long = 1
    private var results: List<Url> = emptyList()

    fun returningSingleResult(result: Long): MockEntityManagerBuilder {
        this.singleResult = result

        return this
    }

    fun returningUrls(results: List<Url>): MockEntityManagerBuilder {
        this.results = results

        return this
    }

    fun build(): EntityManager {
        val em = mockk<EntityManager>()

        query = mockk<Query>()
        every { query.parameters } returns emptySet<Parameter<String>>()
        every { query.singleResult } returns singleResult
        every { query.setMaxResults(any<Int>()) } returns query
        every { query.setFirstResult(any<Int>()) } returns query
        every { query.resultList } returns results
        every { query.setParameter(any<Int>(), any()) } returns query
        every { em.createQuery(any<String>()) } returns query
        every { query.executeUpdate() } returns 1

        return em
    }
}
