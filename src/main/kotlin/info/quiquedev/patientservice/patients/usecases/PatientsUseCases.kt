package info.quiquedev.patientservice.patients.usecases

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import arrow.core.some
import info.quiquedev.patientservice.patients.NewPatientDto
import info.quiquedev.patientservice.patients.PatientDto
import info.quiquedev.patientservice.patients.ReactorUtils.safeMono
import info.quiquedev.patientsservice.patients.usecases.tables.records.PatientsRecord
import reactor.core.publisher.Mono
import java.time.ZoneOffset.UTC

class PatientsUseCases(
    private val repository: PatientsRepository
) {
    class UnexpectedError(t: Throwable?) : Exception(t)

    fun createPatient(newPatientDto: NewPatientDto):
            Mono<PatientDto> =
        repository.createPatient(
            newPatientDto.name,
            newPatientDto.surname,
            newPatientDto.passportNumber
        ).flatMap(::toDto)

    private fun toDto(patientsRecord: PatientsRecord) =
        safeMono {
            PatientDto(
                id = patientsRecord.id!!,
                name = patientsRecord.name!!,
                surname = patientsRecord.surname!!,
                passportNumber = patientsRecord.passportNumber!!,
                createdAt = patientsRecord.createdAt!!.toInstant(UTC)
            )
        }.onErrorMap(::UnexpectedError)

    fun findPatientById(id: String): Mono<Option<PatientDto>> =
        repository.findPatientById(id)
            .flatMap { maybePatientsRecord ->
                when (maybePatientsRecord) {
                    None -> Mono.just(none())
                    is Some -> toDto(maybePatientsRecord.value)
                        .map { it.some() }
                }
            }
}