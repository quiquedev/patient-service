package info.quiquedev.patientservice.patients.usecases

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.RenderNameCase
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import javax.sql.DataSource

@Configuration
class PatientsRepositoryConfig {
    @Bean
    fun dsl(
        dataSource: DataSource
    ): DSLContext = DSL.using(
        dataSource,
        SQLDialect.MYSQL,
        Settings().withRenderNameCase(RenderNameCase.LOWER)
    )

    @Bean
    fun clock(): Clock = Clock.systemUTC()

    @Bean
    fun patientsRepository(
        dslContext: DSLContext,
        clock: Clock
    ) = PatientsRepository(dslContext, clock)
}