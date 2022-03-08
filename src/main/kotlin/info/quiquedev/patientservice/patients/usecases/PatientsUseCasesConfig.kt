package info.quiquedev.patientservice.patients.usecases

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(PatientsRepository::class)
class PatientsUseCasesConfig {
    @Bean
    fun patientsUseCases(
        patientsRepository: PatientsRepository
    ) = PatientsUseCases(patientsRepository)
}