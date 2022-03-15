package info.quiquedev.patientservice.patients

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RequestPredicates.accept
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.RouterFunctions.route
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class PatientsRouter(
    private val patientsHandler: PatientsHandler
) {
    @Bean
    fun createPatient():
            RouterFunction<ServerResponse> = route(
        POST("/patients")
            .and(accept(APPLICATION_JSON)),
        patientsHandler::handlePatientCreation
    )

    @Bean
    fun findPatientById():
            RouterFunction<ServerResponse> = route(
        POST("/patients/{id}")
            .and(accept(APPLICATION_JSON)),
        patientsHandler::handleFindPatientById
    )
}