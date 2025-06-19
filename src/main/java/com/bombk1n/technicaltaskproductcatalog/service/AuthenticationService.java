package com.bombk1n.technicaltaskproductcatalog.service;

import com.bombk1n.technicaltaskproductcatalog.dto.AuthRequest;
import com.bombk1n.technicaltaskproductcatalog.dto.AuthResponse;
import com.bombk1n.technicaltaskproductcatalog.exception.UsernameAlreadyExistsException;
import com.bombk1n.technicaltaskproductcatalog.model.User;
import com.bombk1n.technicaltaskproductcatalog.model.UserRole;
import com.bombk1n.technicaltaskproductcatalog.repository.UserRepository;
import com.bombk1n.technicaltaskproductcatalog.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(AuthRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyExistsException(request.getUsername());
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(UserRole.USER));
        userRepository.save(user);
        return login(new AuthRequest(request.getUsername(), request.getPassword()));
    }

    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        return new AuthResponse(jwtService.generateToken(request.getUsername()));
    }
}
