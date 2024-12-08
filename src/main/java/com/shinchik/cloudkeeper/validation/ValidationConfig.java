package com.shinchik.cloudkeeper.validation;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
public class ValidationConfig {

    @Bean
    @ConfigurationProperties(prefix = "validation")
    public ValidationProperties validationProperties(){
        return new ValidationProperties();
    }


}
