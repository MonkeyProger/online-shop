package org.scenter.onlineshop.web.controllers;

import lombok.RequiredArgsConstructor;
import org.scenter.onlineshop.common.exception.ElementIsPresentedException;
import org.scenter.onlineshop.common.requests.LoginRequest;
import org.scenter.onlineshop.common.requests.SignupRequest;
import org.scenter.onlineshop.common.requests.AdminSignupRequest;
import org.scenter.onlineshop.common.responses.JWTResponse;
import org.scenter.onlineshop.common.responses.MessageResponse;
import org.scenter.onlineshop.security.JWTUtils;
import org.scenter.onlineshop.service.services.UserDetailsImpl;
import org.scenter.onlineshop.service.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder encoder;
    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JWTResponse(jwt,
                userDetails.getId(),
                userDetails.getName(),
                userDetails.getSurname(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) throws ElementIsPresentedException {
        signUpRequest.setPassword(encoder.encode(signUpRequest.getPassword()));
        userDetailsService.registerUser(signUpRequest);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    public ResponseEntity<?> updateUser(@Valid AdminSignupRequest signUpRequest, Long id)
            throws NoSuchElementException{
        signUpRequest.setPassword(encoder.encode(signUpRequest.getPassword()));
        userDetailsService.updateUser(signUpRequest, id);

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }
}