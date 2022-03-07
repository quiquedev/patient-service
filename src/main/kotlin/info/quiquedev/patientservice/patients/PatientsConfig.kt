package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import info.quiquedev.patientservice.patients.usecases.PatientsUsecasesConfig
import liquibase.database.Database
import org.jooq.DSLContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.time.Clock

@Configuration
@Import(PatientsRouter::class, PatientsUsecasesConfig::class)
class PatientsConfig {

    @Bean
    fun patientsHandler(patientsUseCases: PatientsUseCases) =
        PatientsHandler(patientsUseCases)

}