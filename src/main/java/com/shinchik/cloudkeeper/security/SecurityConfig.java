package com.shinchik.cloudkeeper.security;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile("auth")
@Getter
public class SecurityConfig {

    private final String registerUrl = "/auth/register";
    private final String loginUrl = "/auth/login";
    private final String logoutUrl = "/auth/logout";
    private final String welcomeUrl = "/welcome"; // TODO: remove welcome url if decide not to do welcome page
    private final String defaultUrl = "/";
    private final String[] unsecuredUrls = new String[]{
            welcomeUrl,
            "/error",
            "/css/**",
            "/js/**",
            "/common/**",
            "/cloud-data.ico"
    };

    private final UserDetailsService userDetailsService;


    @Autowired
    public SecurityConfig(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(registerUrl, loginUrl).anonymous()
                        .requestMatchers(unsecuredUrls).permitAll()
                        .requestMatchers("/**").authenticated())
                .formLogin(formLogin -> {
                    formLogin.loginPage(loginUrl);
                    formLogin.loginProcessingUrl(loginUrl);
                    formLogin.defaultSuccessUrl(defaultUrl, true);
                    formLogin.failureUrl(loginUrl + "?error");
                })
                .logout(logout -> {
                    logout.logoutUrl(logoutUrl);
                    logout.logoutSuccessUrl(loginUrl);
                })
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
