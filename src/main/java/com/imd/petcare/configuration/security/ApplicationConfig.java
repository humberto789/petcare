package com.imd.petcare.configuration.security;

import com.imd.petcare.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application settings.
 *
 * This class is responsible for configuring various settings and beans for the application.
 */
@Configuration
public class ApplicationConfig {

    private final UserRepository userRepository;

    public ApplicationConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Defines a UserDetailsService bean that retrieves user details from the repository.
     *
     * @return a UserDetailsService that loads user details by username
     * @throws UsernameNotFoundException if the user is not found in the repository
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Defines an AuthenticationProvider bean that uses a DaoAuthenticationProvider with a
     * custom UserDetailsService and PasswordEncoder.
     *
     * @return an AuthenticationProvider configured with a custom UserDetailsService and PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Defines an AuthenticationManager bean that retrieves the authentication manager from
     * the provided AuthenticationConfiguration.
     *
     * @param config the AuthenticationConfiguration to retrieve the AuthenticationManager from
     * @return the AuthenticationManager
     * @throws Exception if an error occurs while retrieving the AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines a PasswordEncoder bean that uses BCryptPasswordEncoder for password encoding.
     *
     * @return a PasswordEncoder that uses BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}