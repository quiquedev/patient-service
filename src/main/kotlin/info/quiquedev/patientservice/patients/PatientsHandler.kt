package info.quiquedev.patientservice.patients

import info.quiquedev.patientservice.patients.usecases.PatientsUseCases
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class PatientsHandler(
    private val patientsUseCases: PatientsUseCases
) {
    fun handlePatientCreation(serverRequest: ServerRequest): Mono<ServerResponse> = TODO()
}
