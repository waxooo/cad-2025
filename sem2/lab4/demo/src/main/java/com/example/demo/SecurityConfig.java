package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Статические ресурсы доступны всем
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        // Публичные страницы
                        .requestMatchers("/", "/home", "/login", "/access-denied").permitAll()

                        // КЛИЕНТЫ
                        // Просмотр клиентов — для всех аутентифицированных
                        .requestMatchers("/clients").authenticated()
                        // Добавление, редактирование, удаление клиентов — только ADMIN
                        .requestMatchers("/clients/add", "/clients/edit/**",
                                "/clients/update/**", "/clients/delete/**",
                                "/clients/view/**").hasRole("ADMIN")

                        // ЗАПЧАСТИ
                        // Просмотр запчастей — для всех аутентифицированных
                        .requestMatchers("/spareparts").authenticated()
                        // Управление запчастями — только ADMIN
                        .requestMatchers("/spareparts/add", "/spareparts/edit/**",
                                "/spareparts/update/**", "/spareparts/delete/**",
                                "/spareparts/restock/**").hasRole("ADMIN")

                        // API (если понадобится)
                        .requestMatchers("/api/**").authenticated()

                        // Всё остальное — только для авторизованных
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?error=true")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/access-denied")
                )

                // Временно отключаем CSRF для упрощения разработки
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();

        UserDetails client = User.builder()
                .username("client")
                .password(encoder.encode("client123"))
                .roles("CLIENT")
                .build();

        return new InMemoryUserDetailsManager(admin, client);
    }
}