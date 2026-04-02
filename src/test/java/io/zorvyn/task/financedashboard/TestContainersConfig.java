package io.zorvyn.task.financedashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfig {

    @Bean
    @ServiceConnection(name = "h2")
    GenericContainer<?> h2Container() {
        return new GenericContainer<>(DockerImageName.parse("h2database/h2:latest"))
                .withExposedPorts(8082)
                .withEnv("H2_OPTIONS", "-ifExists");
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}

