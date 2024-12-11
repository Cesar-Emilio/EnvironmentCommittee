package utez.edu.mx.environmentcommittee.utils.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // Permitir todas las rutas temporalmente
            http
                    .csrf(csrf -> csrf.disable())
                    // Deshabilitar CSRF temporalmente para pruebas
                    .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // Permitir todas las solicitudes

            return http.build();
        }
}

