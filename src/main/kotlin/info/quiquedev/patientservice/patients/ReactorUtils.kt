package info.quiquedev.patientservice.patients

import reactor.core.publisher.Mono

// TODO: remove unused methods when service is fully implemented
object ReactorUtils {
    fun <T> safeMono(callable: () -> T) =
        Mono.defer {
            try {
                callable.invoke()?.let { Mono.just(it) } ?: throw NullPointerException()
            } catch (t: Throwable) {
                Mono.error(t)
            }
        }
}
