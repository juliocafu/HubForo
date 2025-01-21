package com.hubforo.hub_foro.infra.security;

import com.hubforo.hub_foro.usuario.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            var token = authHeader.replace("Bearer ", "");
            var nombreUsuario = tokenService.getSubject(token); // Obtener el subject (username) del token

            if (nombreUsuario != null) {
                // Buscar usuario en la base de datos
                var usuario = usuarioRepository.findByLogin(nombreUsuario).orElse(null);

                if (usuario != null) {
                    // Crear el objeto de autenticación con las autoridades del usuario
                    var authentication = new UsernamePasswordAuthenticationToken(
                            usuario, null, usuario.getAuthorities()
                    );

                    // Establecer el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }

        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }
}