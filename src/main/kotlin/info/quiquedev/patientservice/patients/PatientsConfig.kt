package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.usecases.DatabaseConfig
import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import liquibase.database.Database
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.time.Clock

@Configuration
@Import(PatientsRouter::class, DatabaseConfig::class)
class PatientsConfig {
    @Bean
    fun patientsHandler(patientsUseCases: PatientsUseCases) =
        PatientsHandler(patientsUseCases)

    @Bean
    fun patientsUseCases(
        dslContext: DSLContext,
        clock: Clock
    ) = PatientsUseCases(dslContext, clock)
}