package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(PatientsRouter::class)
class PatientsConfig {
    @Bean
    fun patientsHandler(patientsUseCases: PatientsUseCases) =
        PatientsHandler(patientsUseCases)

    @Bean
    fun patientsUseCases() = PatientsUseCases()
}