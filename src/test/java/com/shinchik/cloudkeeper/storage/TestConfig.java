package com.shinchik.cloudkeeper.storage;

import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

//@TestConfiguration
//@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
public class TestConfig {

//    @Bean
//    public static PropertySourcesPlaceholderConfigurer properties() {
//        PropertySourcesPlaceholderConfigurer pspc = new PropertySourcesPlaceholderConfigurer();
//        Resource[] resources = new ClassPathResource[]{new ClassPathResource("application.yaml")};
//        pspc.setLocations(resources);
//        pspc.setIgnoreUnresolvablePlaceholders(true);
//        return pspc;
//    }


}
