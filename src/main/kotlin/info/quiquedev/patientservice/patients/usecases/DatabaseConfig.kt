package info.quiquedev.patientservice.patients.usecases

import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.RenderNameCase
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    @Bean
    fun dsl(
        dataSource: DataSource
    ): DSLContext = DSL.using(
        dataSource,
        SQLDialect.MYSQL,
        Settings().withRenderNameCase(RenderNameCase.LOWER)
    )
}