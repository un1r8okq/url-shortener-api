package land.ver.url.shortener.integration.repositories.postgresql

import jakarta.transaction.Transactional
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@Transactional
@ActiveProfiles("test")
abstract class BaseRepositoryTest
