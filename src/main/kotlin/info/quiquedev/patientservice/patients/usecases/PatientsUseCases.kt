package info.quiquedev.patientservice.patients.usecases

import info.quiquedev.patientservice.patients.NewPatientDto
import info.quiquedev.patientservice.patients.PatientDto
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.Clock

class PatientsUseCases(
    private val dsl: DSLContext,
    private val clock: Clock
) {
    fun createPatient(newPatientDto: NewPatientDto): Mono<PatientDto> = TODO()


}
