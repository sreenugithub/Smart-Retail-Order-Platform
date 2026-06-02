package com.retail.order_service.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaConnectionDetails;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.autoconfigure.kafka.SslBundleSslEngineFactory;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.util.StringUtils;

@Configuration
public class KafkaSslStoreConfig {

    private static final String CLASSPATH_PREFIX = "classpath:";

    @Bean
    @ConditionalOnMissingBean
    KafkaAdmin kafkaAdmin(KafkaProperties kafkaProperties, KafkaConnectionDetails connectionDetails,
            ResourceLoader resourceLoader) {
        Map<String, Object> properties = kafkaProperties.buildAdminProperties(null);
        applyConnectionDetails(properties, connectionDetails.getAdmin());
        normalizeStoreLocations(properties, resourceLoader);

        KafkaAdmin kafkaAdmin = new KafkaAdmin(properties);
        KafkaProperties.Admin admin = kafkaProperties.getAdmin();
        if (admin.getCloseTimeout() != null) {
            kafkaAdmin.setCloseTimeout((int) admin.getCloseTimeout().getSeconds());
        }
        if (admin.getOperationTimeout() != null) {
            kafkaAdmin.setOperationTimeout((int) admin.getOperationTimeout().getSeconds());
        }
        kafkaAdmin.setFatalIfBrokerNotAvailable(admin.isFailFast());
        kafkaAdmin.setModifyTopicConfigs(admin.isModifyTopicConfigs());
        kafkaAdmin.setAutoCreate(admin.isAutoCreate());
        return kafkaAdmin;
    }

    @Bean
    @ConditionalOnMissingBean(ProducerFactory.class)
    DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(KafkaProperties kafkaProperties,
            KafkaConnectionDetails connectionDetails, ResourceLoader resourceLoader,
            ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers) {
        Map<String, Object> properties = kafkaProperties.buildProducerProperties(null);
        applyConnectionDetails(properties, connectionDetails.getProducer());
        normalizeStoreLocations(properties, resourceLoader);

        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(properties);
        String transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    private static void applyConnectionDetails(Map<String, Object> properties,
            KafkaConnectionDetails.Configuration connectionDetails) {
        properties.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, connectionDetails.getBootstrapServers());
        if (StringUtils.hasLength(connectionDetails.getSecurityProtocol())) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, connectionDetails.getSecurityProtocol());
        }
        SslBundle sslBundle = connectionDetails.getSslBundle();
        if (sslBundle != null) {
            properties.put(SslConfigs.SSL_ENGINE_FACTORY_CLASS_CONFIG, SslBundleSslEngineFactory.class);
            properties.put(SslBundle.class.getName(), sslBundle);
        }
    }

    private static void normalizeStoreLocations(Map<String, Object> properties, ResourceLoader resourceLoader) {
        normalizeStoreLocation(properties, SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, resourceLoader);
        normalizeStoreLocation(properties, SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, resourceLoader);
    }

    private static void normalizeStoreLocation(Map<String, Object> properties, String key,
            ResourceLoader resourceLoader) {
        Object value = properties.get(key);
        if (value instanceof String location && location.startsWith(CLASSPATH_PREFIX)) {
            properties.put(key, copyClasspathResourceToTempFile(location, resourceLoader));
        }
    }

    private static String copyClasspathResourceToTempFile(String location, ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(location);
        if (!resource.exists()) {
            throw new IllegalStateException("Kafka SSL store resource does not exist: " + location);
        }

        String filename = resource.getFilename();
        String suffix = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf('.'))
                : ".store";
        try (InputStream inputStream = resource.getInputStream()) {
            Path tempFile = Files.createTempFile("kafka-ssl-store-", suffix);
            Files.copy(inputStream, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            return tempFile.toAbsolutePath().toString();
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to prepare Kafka SSL store resource: " + location, ex);
        }
    }
}
