package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.ApplicationStartupListener;
import com.shinchik.cloudkeeper.storage.controller.HomeController;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@TestConfiguration
public class MinioServiceTestConfig {

    @MockBean
    private DataSource dataSource;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private HomeController homeController;

    @MockBean
    private ApplicationStartupListener applicationStartupListener;
//
//    @MockBean
//    private AuthController baseController;

    //
//    @MockBean
//    private DaoAuthenticationProvider daoAuthenticationProvider;
//

//    @MockBean
//    private HibernateJpaAutoConfiguration hibernateJpaConfiguration;


}
