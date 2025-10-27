package com.example.hardwaremanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Developer-only Mongo configuration that points to a local MongoDB instance by default.
 *
 * This configuration is active only when the "dev" Spring profile is enabled.
 * In all other environments, Spring Boot's auto-configuration will use
 * the connection string defined in application.properties (e.g., MongoDB Atlas URI).
 */
@Profile("dev")
@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "athukorala_traders_db";
    }
}
