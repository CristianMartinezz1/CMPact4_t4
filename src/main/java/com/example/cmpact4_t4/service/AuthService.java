package com.example.cmpact4_t4.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.cmpact4_t4.dto.AuthDtos.AuthResponse;
import com.example.cmpact4_t4.dto.AuthDtos.LoginRequest;
import com.example.cmpact4_t4.dto.AuthDtos.RegisterRequest;
import com.example.cmpact4_t4.dto.AuthDtos.UsuarioResponse;
import com.example.cmpact4_t4.entity.Usuario;
import com.example.cmpact4_t4.exception.ConflictoException;
import com.example.cmpact4_t4.exception.RecursoNoEncontradoException;
import com.example.cmpact4_t4.repository.UsuarioRepository;
import com.example.cmpact4_t4.security.JwtService;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public UsuarioResponse registrar(RegisterRequest datos) {
        String email = datos.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictoException("Ya existe una cuenta con el correo " + email);
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(datos.nombre().trim());
        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(datos.password())); // se guarda cifrada con BCrypt
        return aRespuesta(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest datos) {
        // Si el correo o la contraseña no coinciden, lanza BadCredentialsException (-> 401)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(datos.email().trim().toLowerCase(), datos.password()));
        Usuario usuario = buscarPorEmail(datos.email());
        return new AuthResponse(jwtService.generarToken(usuario), "Bearer",
                jwtService.getExpiracionSegundos(), aRespuesta(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse perfil(String email) {
        return aRespuesta(buscarPorEmail(email));
    }

    private Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
    }

    private UsuarioResponse aRespuesta(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getEmail(), u.getRol());
    }
}
