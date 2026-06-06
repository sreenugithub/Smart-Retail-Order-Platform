package com.retail.inventory_service.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class KafkaSslStoreLocationEnvironmentPostProcessor implements
        EnvironmentPostProcessor,
        ApplicationListener<ApplicationEnvironmentPreparedEvent>,
        Ordered {

    private static final String PROPERTY_SOURCE_NAME = "kafkaSslStoreLocationOverrides";

    private static final String CLASSPATH_PREFIX = "classpath:";

    private static final List<String> SSL_LOCATION_PROPERTIES = List.of(
            "spring.kafka.ssl.key-store-location",
            "spring.kafka.ssl.trust-store-location",
            "spring.kafka.properties.ssl.keystore.location",
            "spring.kafka.properties.ssl.truststore.location",
            "spring.kafka.consumer.properties.ssl.keystore.location",
            "spring.kafka.consumer.properties.ssl.truststore.location",
            "spring.kafka.producer.properties.ssl.keystore.location",
            "spring.kafka.producer.properties.ssl.truststore.location",
            "spring.kafka.admin.properties.ssl.keystore.location",
            "spring.kafka.admin.properties.ssl.truststore.location",
            "spring.kafka.streams.properties.ssl.keystore.location",
            "spring.kafka.streams.properties.ssl.truststore.location"
    );

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> overrides = new LinkedHashMap<>();

        for (String property : SSL_LOCATION_PROPERTIES) {
            String location = environment.getProperty(property);
            if (location != null && location.startsWith(CLASSPATH_PREFIX)) {
                Path extractedResource = extractClasspathResource(location);
                overrides.put(property, property.startsWith("spring.kafka.ssl.")
                        ? extractedResource.toUri().toString()
                        : extractedResource.toAbsolutePath().toString());
            }
        }

        if (!overrides.isEmpty()) {
            environment.getPropertySources().addFirst(new MapPropertySource(PROPERTY_SOURCE_NAME, overrides));
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        postProcessEnvironment(event.getEnvironment(), event.getSpringApplication());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

    private Path extractClasspathResource(String location) {
        String resourceName = location.substring(CLASSPATH_PREFIX.length());
        while (resourceName.startsWith("/")) {
            resourceName = resourceName.substring(1);
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IllegalStateException("Kafka SSL resource not found on the classpath: " + location);
        }

        try (inputStream) {
            String fileName = Path.of(resourceName).getFileName().toString();
            Path target = Files.createTempFile("kafka-ssl-", "-" + fileName);
            Files.copy(inputStream, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            target.toFile().deleteOnExit();
            return target;
        }
        catch (IOException ex) {
            throw new IllegalStateException("Kafka SSL resource could not be extracted: " + location, ex);
        }
    }
}
