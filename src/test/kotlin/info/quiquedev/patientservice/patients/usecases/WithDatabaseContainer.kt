package info.quiquedev.patientservice.patients.usecases

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MySQLContainer

interface WithDatabaseContainer {
    companion object {
        class MySQLContainerK(image: String) : MySQLContainer<MySQLContainerK>(image)

        private var dbContainer: MySQLContainerK = MySQLContainerK("mysql:5.7.32")
            .withReuse(true)

        init {
            dbContainer.start()
        }

        @DynamicPropertySource
        @JvmStatic
        fun datasourceConfig(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", dbContainer::getJdbcUrl)
            registry.add("spring.datasource.hikari.password", dbContainer::getPassword)
            registry.add("spring.datasource.hikari.username", dbContainer::getUsername)
            registry.add("spring.datasource.initialization-mode") { "always" }
        }
    }
}
