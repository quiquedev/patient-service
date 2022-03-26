package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.ReactorUtils.safeMono
import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.ServerResponse.status
import reactor.core.publisher.Mono
import java.net.URI
import javax.validation.ConstraintViolation
import javax.validation.Validation
import javax.validation.Validator

class PatientsHandler(
    private val useCases: PatientsUseCases
) {
    fun handlePatientCreation(serverRequest: ServerRequest): Mono<ServerResponse> =
        parseBody(serverRequest)
            .flatMap(::validate)
            .flatMap(useCases::createPatient)
            .flatMap { created(URI.create("/patients/${it.id}")).bodyValue(it) }
            .onErrorResume(::handlePatientCreationError)

    fun handleFindPatientById(serverRequest: ServerRequest): Mono<ServerResponse> =
        extractPatientId(serverRequest)
            .flatMap(useCases::findPatientById)
            .flatMap { maybePatient ->
                maybePatient.fold({ notFound().build() }, { ok().bodyValue(it) })
            }
            .onErrorResume {
                status(INTERNAL_SERVER_ERROR).build()
            }

    private companion object {
        const val PATH_VARIABLE_ID = "id"

        class ValidationError(val errors: Set<String>) : Throwable()
        class ParseError(t: Throwable, override val message: String = "request body cannot be parsed") :
            Throwable(t)

        val VALIDATOR: Validator = Validation.buildDefaultValidatorFactory().validator
        fun parseBody(serverRequest: ServerRequest): Mono<NewPatientDto> =
            serverRequest.bodyToMono(NewPatientDto::class.java)
                .onErrorMap(Companion::ParseError)

        fun validate(dto: NewPatientDto): Mono<NewPatientDto> =
            safeMono { VALIDATOR.validate(dto) }
                .flatMap { constraintViolations: MutableSet<ConstraintViolation<NewPatientDto>> ->
                    if (constraintViolations.isEmpty()) Mono.just(dto)
                    else Mono.error(ValidationError(constraintViolations.map { it.message }.toSet()))
                }

        fun handlePatientCreationError(t: Throwable): Mono<ServerResponse> =
            when (t) {
                is ParseError -> {
                    status(BAD_REQUEST)
                        .bodyValue(
                            RestErrorDto(
                                "request body cannot be parsed",
                                t.cause?.message?.let { setOf(it) }
                            )
                        )
                }
                is ExistingPassportNumberError -> {
                    status(BAD_REQUEST)
                        .bodyValue(
                            RestErrorDto("existing passport number '${t.passportNumber}'")
                        )
                }
                is ValidationError -> {
                    status(BAD_REQUEST)
                        .bodyValue(
                            RestErrorDto(
                                message = "request body is not valid",
                                errors = t.errors
                            )
                        )
                }
                else -> status(INTERNAL_SERVER_ERROR).build()
            }

        fun extractPatientId(serverRequest: ServerRequest) =
            safeMono {
                serverRequest.pathVariable(PATH_VARIABLE_ID)
            }
    }
}
