package org.scenter.onlineshop.controllers;

import lombok.RequiredArgsConstructor;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.ERole;
import org.scenter.onlineshop.exception.ElementIsPresentedException;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.requests.AdminSignupRequest;
import org.scenter.onlineshop.requests.LoginRequest;
import org.scenter.onlineshop.requests.SignupRequest;
import org.scenter.onlineshop.responses.JWTResponse;
import org.scenter.onlineshop.responses.MessageResponse;
import org.scenter.onlineshop.security.JWTUtils;
import org.scenter.onlineshop.services.UserDetailsImpl;
import org.scenter.onlineshop.services.UserDetailsServiceImpl;
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
import java.util.Optional;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private final UserRepo userRepo;
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
                userDetails.getUsername(),
                userDetails.getSurname(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody SignupRequest signUpRequest) throws ElementIsPresentedException {
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new ElementIsPresentedException("Error: Email is already in use!");
        }

        ERole role;
        if (signUpRequest instanceof AdminSignupRequest) {
            role = getERole(((AdminSignupRequest) signUpRequest).getRole());
        } else {
            role = ERole.ROLE_USER;
        }

        AppUser user = new AppUser(
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                role);

        userDetailsService.saveUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    private ERole getERole(String role) {
        if (role == null) {
            return ERole.ROLE_USER;
        }
        switch (role) {
            case "admin":
                return ERole.ROLE_ADMIN;
            case "user":
                return ERole.ROLE_USER;
            default:
                throw new EnumConstantNotPresentException(ERole.class, "No such role: " + role);
        }
    }

    public ResponseEntity<?> updateUser(@Valid AdminSignupRequest signUpRequest, Long id) {
        Optional<AppUser> user = userRepo.findById(id);
        if (!user.isPresent()) {
            throw new NoSuchElementException("User with id " + id + " is not presented in database");
        }

        AppUser updatedUser = user.get();
        ERole role = getERole(signUpRequest.getRole());
        updatedUser.setRole(role);
        updatedUser.setName(signUpRequest.getName());
        updatedUser.setSurname(signUpRequest.getSurname());
        updatedUser.setEmail(signUpRequest.getEmail());
        updatedUser.setPassword(encoder.encode(signUpRequest.getPassword()));

        userDetailsService.saveUser(updatedUser);

        return ResponseEntity.ok(new MessageResponse("User updated successfully!"));
    }
}