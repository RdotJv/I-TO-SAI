package com.example.security_practice.confg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        DataSourceProperties properties = new DataSourceProperties();
        
        // Handle Render's DATABASE_URL format
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            // Convert postgresql:// to jdbc:postgresql://
            if (databaseUrl.startsWith("postgresql://")) {
                properties.setUrl("jdbc:" + databaseUrl);
            } else if (databaseUrl.startsWith("jdbc:postgresql://")) {
                properties.setUrl(databaseUrl);
            } else {
                // Fallback to the original URL
                properties.setUrl(databaseUrl);
            }
        }
        
        return properties;
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }
} 