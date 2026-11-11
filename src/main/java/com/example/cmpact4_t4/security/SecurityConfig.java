package com.example.cmpact4_t4.security;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.cmpact4_t4.repository.UsuarioRepository;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class SecurityConfig {

    // Cadena de filtros de Spring Security
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JsonAuthenticationEntryPoint entryPoint) throws Exception {
        http
                // API REST sin sesiones ni cookies: no hace falta CSRF
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas: registro, login y la info básica de la API
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/", "/api").permitAll()
                        .requestMatchers("/error").permitAll()
                        // Todo lo demás exige un JWT válido
                        .anyRequest().authenticated())
                // Filtro que lee "Authorization: Bearer <token>", valida la firma y la expiración
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> { })
                        .authenticationEntryPoint(entryPoint))
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint))
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable());
        return http.build();
    }

    // Clave secreta para firmar y verificar los tokens (HS256)
    @Bean
    public SecretKey jwtSecretKey(@Value("${app.jwt.secret}") String secreto) {
        byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret (JWT_SECRET) debe tener al menos 32 caracteres");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        // Además de la firma, valida que no esté vencido y que lo haya emitido esta API
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(JwtService.EMISOR));
        return decoder;
    }

    // Cifrado de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Cómo encuentra Spring Security a un usuario al hacer login
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return email -> usuarioRepository.findByEmailIgnoreCase(email)
                .map(u -> User.withUsername(u.getEmail()).password(u.getPassword()).roles(u.getRol()).build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    // Compara el correo y la contraseña del login contra la base de datos
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }
}
