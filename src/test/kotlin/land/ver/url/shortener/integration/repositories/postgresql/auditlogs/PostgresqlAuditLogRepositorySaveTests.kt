package land.ver.url.shortener.integration.repositories.postgresql.auditlogs

import land.ver.url.shortener.LogType
import land.ver.url.shortener.integration.repositories.postgresql.BaseRepositoryTest
import land.ver.url.shortener.models.NewAuditLog
import land.ver.url.shortener.repositories.postgresql.PostgresqlAuditLogsRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired

class PostgresqlAuditLogRepositorySaveTests(
    @Autowired private val repository: PostgresqlAuditLogsRepository,
) : BaseRepositoryTest() {
    @Test
    fun `the ID is generated correctly`() {
        val newAuditLog = NewAuditLog(
            logType = LogType.URL_VISITED,
            message = "",
        )

        val result = repository.save(newAuditLog)

        assertNotNull(result.id)
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "Something happened successfully",
            "♨\uFE0F"
        ],
    )
    fun `the audit log message is stored correctly`(message: String) {
        val newAuditLog = NewAuditLog(
            logType = LogType.URL_SHORTENED,
            message = message,
        )

        val result = repository.save(newAuditLog)

        assertEquals(message, result.message)
    }

    @Test
    fun `when the audit log type is URL_SHORTENED, it is stored correctly`() {
        val newAuditLog = NewAuditLog(
            logType = LogType.URL_SHORTENED,
            message = "",
        )

        val result = repository.save(newAuditLog)

        assertEquals(LogType.URL_SHORTENED, result.logType)
    }

    @Test
    fun `when the audit log type is URL_VISITED, it is stored correctly`() {
        val newAuditLog = NewAuditLog(
            logType = LogType.URL_VISITED,
            message = "",
        )

        val result = repository.save(newAuditLog)

        assertEquals(LogType.URL_VISITED, result.logType)
    }
}
