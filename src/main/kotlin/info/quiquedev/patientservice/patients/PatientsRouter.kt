package info.quiquedev.patientservice.patients

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerResponse

@Configuration
class PatientsRouter(
    private val patientsHandler: PatientsHandler
) {
    @Bean
    fun createPatient():
            RouterFunction<ServerResponse> = RouterFunctions.route(
        RequestPredicates.POST("/patients")
            .and(RequestPredicates.accept(MediaType.APPLICATION_JSON)),
        patientsHandler::handlePatientCreation
    )
}