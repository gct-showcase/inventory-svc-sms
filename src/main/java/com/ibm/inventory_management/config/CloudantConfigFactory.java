package com.ibm.inventory_management.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Profile("cloudant")
@Component
public class CloudantConfigFactory {
    private static final Logger log = LoggerFactory.getLogger(CloudantConfigFactory.class);

    @Bean
    public CloudantConfig buildCloudantConfig() throws IOException {
        return buildConfigFromBinding(
                loadCloudantConfig(),
                loadDatabaseName()
        );
    }

    protected String loadCloudantConfig() throws IOException {
        if (System.getProperty("CLOUDANT_CONFIG") != null) {
            log.warn("Config not found at CLOUDANT_CONFIG");
        }

       return System.getProperty("CLOUDANT_CONFIG") != null
                ? System.getProperty("CLOUDANT_CONFIG")
                : loadCloudantMappingFromLocalDev().getCloudantConfig();
    }

    protected CloudantMapping loadCloudantMappingFromLocalDev() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(
                this.getClass().getClassLoader().getResourceAsStream("mappings.json"),
                CloudantMapping.class
        );
    }

    protected String loadDatabaseName() throws IOException {
        return System.getProperty("DATABASE_NAME") != null
                ? System.getProperty("DATABASE_NAME")
                : loadCloudantMappingFromLocalDev().getDatabaseName();
    }

    protected CloudantConfig buildConfigFromBinding(String binding, String databaseName) throws IOException {
        final ObjectMapper mapper = new ObjectMapper();

        if (binding == null) {
            return new CloudantConfig();
        }

        log.debug("Building cloudant config from value: " + binding);

        final CloudantConfig baseConfig = mapper.readValue(binding, CloudantConfig.class);

        if (baseConfig == null) {
            log.warn("Unable to parse Cloudant config: " + binding);
            return new CloudantConfig()
                .withDatabaseName(databaseName)
                .withUrl("https://0e66b895-2249-4980-b392-fb6e2a40370c-bluemix.cloudantnosqldb.appdomain.cloud")
                .withUsername("0e66b895-2249-4980-b392-fb6e2a40370c-bluemix")
                .withApikey("uebCSAZv8pO4kdzuf4nvtqgLWWsL6hSDdZZHmQl_-jB_");
        }

        return baseConfig.withDatabaseName(databaseName);
    }
}
