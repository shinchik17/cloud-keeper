package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.storage.controller.BaseController;
import com.shinchik.cloudkeeper.user.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@TestConfiguration
public class TestStorageConfig {

    @MockBean
    private DataSource dataSource;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private BaseController baseController;
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