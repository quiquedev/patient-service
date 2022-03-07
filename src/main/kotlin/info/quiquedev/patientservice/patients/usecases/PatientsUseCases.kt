package info.quiquedev.patientservice.patients.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import info.quiquedev.patientservice.patients.NewPatientDto
import info.quiquedev.patientservice.patients.PatientDto
import info.quiquedev.patientservice.patients.ReactorUtils.safeMono
import info.quiquedev.patientsservice.patients.usecases.tables.references.PATIENTS
import org.jooq.DSLContext
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZoneOffset.UTC
import java.util.Optional
import java.util.UUID
import java.util.concurrent.CompletionStage

class PatientsUseCases(
    private val dsl: DSLContext,
    private val clock: Clock
) {

    sealed class PatientsUsecasesError(message: String, throwable: Throwable? = null) : Throwable(message, throwable)
    data class ExistingPassportNumberError(val passportNumber: String) :
        PatientsUsecasesError("existing passport number $passportNumber")

    data class UnexpectedError(val throwable: Throwable) :
        PatientsUsecasesError("unexpected error", throwable)

    data class TooManyPatientsError(val id: String, val amount: Int) :
        PatientsUsecasesError("$amount patients found for id $id")

    data class PatientNotFoundError(val id: String) :
        PatientsUsecasesError("patient with id $id not found")

    fun createPatient(newPatientDto: NewPatientDto):
            Mono<PatientDto> =
        Mono
            .fromFuture {
                dsl.transactionResultAsync {
                    if (existsPassportNumber(newPatientDto))
                        throw ExistingPassportNumberError(newPatientDto.passportNumber)
                    val id = UUID.randomUUID().toString()
                    val createdAt = Instant.now(clock)

                    storeNewPatient(id, createdAt, newPatientDto)

                    PatientDto(
                        id = id,
                        name = newPatientDto.name,
                        surname = newPatientDto.surname,
                        passportNumber = newPatientDto.passportNumber,
                        createdAt = createdAt
                    )
                }.toCompletableFuture()
            }
            .onErrorMap {
                when (it) {
                    is ExistingPassportNumberError -> it
                    else -> UnexpectedError(it)
                }
            }

    fun findPatient(id: String): Mono<Optional<PatientDto>> =
        safeMono {
            val patients = dsl
                .selectFrom(PATIENTS)
                .where(PATIENTS.ID.eq(id))
                .toSet()

            when (patients.size) {
                0 -> Optional.empty()
                1 -> {
                    val patient = patients.first()
                    val patientDto = PatientDto(
                        id = patient.id!!,
                        name = patient.name!!,
                        surname = patient.surname!!,
                        passportNumber = patient.passportNumber!!,
                        createdAt = patient.createdAt!!.toInstant(UTC)
                    )
                    Optional.of(patientDto)
                }
                else -> throw TooManyPatientsError(id, patients.size)
            }
        }


    private fun storeNewPatient(
        id: String,
        createdAt: Instant?,
        newPatientDto: NewPatientDto
    ) {
        val newPatient = dsl.newRecord(PATIENTS)
        newPatient.id = id
        newPatient.createdAt = LocalDateTime.ofInstant(createdAt, ZoneId.of("UTC"))
        newPatient.name = newPatientDto.name
        newPatient.surname = newPatientDto.surname
        newPatient.passportNumber = newPatientDto.passportNumber
        newPatient.store()
    }

    private fun existsPassportNumber(newPatientDto: NewPatientDto) =
        dsl.select(PATIENTS.ID)
            .from(PATIENTS)
            .where(PATIENTS.PASSPORT_NUMBER.eq(newPatientDto.passportNumber))
            .fetch()
            .toSet()
            .isNotEmpty()
}