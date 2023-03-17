package org.scenter.onlineshop.controllers;

import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.ERole;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.repo.RoleRepo;
import org.scenter.onlineshop.repo.UserRepo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    UserDetailsServiceImpl userService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JWTUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;

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
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        AppUser user = new AppUser(signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles = signUpRequest.getRoles();

        user.setRoles(buildRoles(strRoles));
        userService.saveUser(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    private Role findRole (ERole enumRole){
        return roleRepo.findByName(enumRole)
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
    }
    private Set<Role> buildRoles (Set<String> strRoles){
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = findRole(ERole.ROLE_USER);
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = findRole(ERole.ROLE_ADMIN);
                        roles.add(adminRole);
                    }
                    case "mod" -> {
                        Role modRole = findRole(ERole.ROLE_MODERATOR);
                        roles.add(modRole);
                    }
                    default -> {
                        Role userRole = findRole(ERole.ROLE_USER);
                        roles.add(userRole);
                    }
                }
            });
        }
        return roles;
    }
}