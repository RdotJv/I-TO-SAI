package com.example.security_practice.confg;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("production")
public class DatabaseConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${DB_USERNAME:}")
    private String username;

    @Value("${DB_PASSWORD:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() {
        // Transform the URL if needed
        String jdbcUrl = databaseUrl;
        if (databaseUrl != null && !databaseUrl.isEmpty() && !databaseUrl.startsWith("jdbc:")) {
            jdbcUrl = "jdbc:" + databaseUrl;
        }
        
        return DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
} 