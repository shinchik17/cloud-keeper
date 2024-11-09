package com.shinchik.cloudkeeper.storage;

import com.shinchik.cloudkeeper.controller.BaseController;
import com.shinchik.cloudkeeper.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@TestConfiguration
public class TestConfigMock {

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
