package ru.otus.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.otus.service.UserService;

@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    public SecurityConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.csrf(csrf -> csrf.disable())
            .authorizeRequests(auth -> {
                auth.antMatchers("/login", "/logout", "/error").permitAll();
                auth.antMatchers("/books").hasAnyRole("ADMIN", "USER", "GUEST"); // смотреть книги могут все
                auth.antMatchers("/books/*").hasRole("ADMIN"); // редактировать книги - только админы
                auth.antMatchers("/books/**/comments/**").hasAnyRole("ADMIN", "USER"); // делать что-то с комментариями - админы и юзеры
                auth.antMatchers("/**").denyAll();
            })
            .formLogin().defaultSuccessUrl("/books").failureForwardUrl("/error")
            .and()
            .rememberMe().key("MySecret").tokenValiditySeconds(60 * 15)
            .and()
            .logout().permitAll().logoutSuccessUrl("/login").invalidateHttpSession(true);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}
