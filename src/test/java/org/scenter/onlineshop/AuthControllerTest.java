package org.scenter.onlineshop;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.scenter.onlineshop.controllers.AuthController;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.scenter.onlineshop.domain.ERole.ROLE_ADMIN;
import static org.scenter.onlineshop.domain.ERole.ROLE_USER;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthControllerTest {
    @MockBean
    private UserRepo userRepo;
    @MockBean
    private UserDetailsServiceImpl userDetailsService;
    @MockBean
    private PasswordEncoder encoder;
    @MockBean
    private JWTUtils jwtUtils;
    @MockBean
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthController authController;

    @Test
    public void authenticateUser() {
        AppUser user = new AppUser(1L,"aaa","aaa","aaa@aaa.aaa",encoder.encode("correct_password"), ROLE_ADMIN);
        LoginRequest loginRequest = new LoginRequest("aaa@aaa.aaa","correct_password");
        UserDetailsImpl mockUserDetails = UserDetailsImpl.build(user);
        Authentication mockAuth = new UsernamePasswordAuthenticationToken(mockUserDetails,null);
        List<String> mockRoles = mockUserDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        String mockJWT = "JWTforTEST";
        ResponseEntity<?> expectedResponse = ResponseEntity.ok(new JWTResponse(mockJWT,
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                mockRoles));

        Mockito.doReturn(mockAuth).when(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        Mockito.doReturn(mockJWT).when(jwtUtils).generateJwtToken(ArgumentMatchers.any());
        ResponseEntity<?> res = authController.authenticateUser(loginRequest);
        Assert.assertEquals(expectedResponse,res);
    }

    @Test
    public void registerUser_Correct() {
        SignupRequest signUpRequest = new SignupRequest(
                "aaaaaa",
                "aaaaaa",
                "aaa@aaa.aaa",
                "aaaaaa");
        AppUser user = new AppUser(
                signUpRequest.getName(),
                signUpRequest.getSurname(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                ERole.ROLE_USER);
        Mockito.doReturn(false).when(userRepo).existsByEmail(signUpRequest.getEmail());
        Mockito.doReturn(this.encoder.encode(signUpRequest.getPassword())).when(encoder).encode(signUpRequest.getPassword());
        try {
            ResponseEntity<?> res = authController.registerUser(signUpRequest);
            Mockito.verify(userDetailsService, Mockito.times(1)).saveUser(user);
            Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
            Assert.assertEquals(new MessageResponse("User registered successfully!"),res.getBody());
        } catch(Exception e) {
            Assert.fail("Не ожидалось исключения: "+e.getMessage());
        }
    }
    @Test
    public void registerAdmin_Correct() {
        AdminSignupRequest adminSignupRequest = new AdminSignupRequest(
                "aaaaaa",
                "aaaaaa",
                "aaa@aaa.aaa",
                "aaaaaa",
                "admin");
        AppUser user = new AppUser(
                adminSignupRequest.getName(),
                adminSignupRequest.getSurname(),
                adminSignupRequest.getEmail(),
                encoder.encode(adminSignupRequest.getPassword()),
                ROLE_ADMIN);
        Mockito.doReturn(false).when(userRepo).existsByEmail(adminSignupRequest.getEmail());
        Mockito.doReturn(this.encoder.encode(adminSignupRequest.getPassword())).when(encoder).encode(adminSignupRequest.getPassword());
        try {
            ResponseEntity<?> res = authController.registerUser(adminSignupRequest);
            Mockito.verify(userDetailsService, Mockito.times(1)).saveUser(user);
            Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
            Assert.assertEquals(new MessageResponse("User registered successfully!"),res.getBody());
        } catch(Exception e) {
            Assert.fail("Не ожидалось исключения: "+e.getMessage());
        }
    }

    @Test
    public void register_EmailIsInUseException() {
        AdminSignupRequest adminSignupRequest = new AdminSignupRequest(
                "aaaaaa",
                "aaaaaa",
                "aaa@aaa.aaa",
                "aaaaaa",
                "admin");
        SignupRequest signUpRequest = new SignupRequest(
                "aaaaaa",
                "aaaaaa",
                "aaa@aaa.aaa",
                "aaaaaa");
        Mockito.doReturn(true).when(userRepo).existsByEmail(adminSignupRequest.getEmail());
        Mockito.doReturn(true).when(userRepo).existsByEmail(signUpRequest.getEmail());

        ElementIsPresentedException e = Assert.assertThrows(ElementIsPresentedException.class,
                () -> authController.registerUser(adminSignupRequest));
        Assert.assertEquals("Error: Email is already in use!", e.getMessage());
        e = Assert.assertThrows(ElementIsPresentedException.class,
                () -> authController.registerUser(signUpRequest));
        Assert.assertEquals("Error: Email is already in use!", e.getMessage());
    }

    @Test
    public void registerAdmin_manyRoles() {
        List<String> roles = Stream.of(null, "admin", "user", "fakeRole")
                .collect(Collectors.toCollection(
                        ArrayList::new));
        AdminSignupRequest request = new AdminSignupRequest(
                "aaaaaa",
                "aaaaaa",
                "aaa@aaa.aaa",
                "aaaaaa",
                "");
        Mockito.doReturn(false).when(userRepo).existsByEmail(request.getEmail());
        Mockito.doReturn(this.encoder.encode(request.getPassword())).when(encoder).encode(request.getPassword());
        roles.forEach(role -> {
            request.setRole(role);
            try {
                ResponseEntity<?> res = authController.registerUser(request);
                Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
                Assert.assertEquals(new MessageResponse("User registered successfully!"),res.getBody());
            } catch(Exception e) {
                if (!role.equals("fakeRole")){
                    Assert.fail("Не ожидалось исключения: "+e.getMessage());
                }
                Assert.assertEquals("No such role: " + role, e.getMessage());
            }
        });
    }

    @Test
    public void updateUser_Correct() {
        Long id = 2L;
        String encodedPassword = encoder.encode("bbbbbb");
        Optional<AppUser> user = Optional.of(new AppUser(
                2L,
                "aaa",
                "aaa",
                "aaa@aaa.aaa",
                encoder.encode("correct_password"),
                ROLE_ADMIN));
        AdminSignupRequest request = new AdminSignupRequest(
                "bbbbbb",
                "bbbbbb",
                "bbb@bbb.bbb",
                "bbbbbb",
                null);
        AppUser updatedUser = new AppUser(2L,
                "bbbbbb",
                "bbbbbb",
                "bbb@bbb.bbb",
                encodedPassword,
                ROLE_USER);
        Mockito.doReturn(user).when(userRepo).findById(id);
        Mockito.doReturn(encodedPassword).when(encoder).encode(ArgumentMatchers.any());
        try {
            ResponseEntity<?> res = authController.updateUser(request,id);
            Mockito.verify(userDetailsService, Mockito.times(1))
                    .saveUser(updatedUser);
            Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
            Assert.assertEquals(new MessageResponse("User updated successfully!"),res.getBody());
        } catch(Exception e) {
            Assert.fail("Не ожидалось исключения: "+e.getMessage());
        }
    }

    @Test
    public void updateUser_UserNotFoundExceptionThrow() {
        Long id = 2L;
        Optional<AppUser> user = Optional.empty();
        AdminSignupRequest request = new AdminSignupRequest(
                "bbbbbb",
                "bbbbbb",
                "bbb@bbb.bbb",
                "bbbbbb",
                null);
        Mockito.doReturn(user).when(userRepo).findById(id);
        NoSuchElementException e = Assert.assertThrows(NoSuchElementException.class,
                () -> authController.updateUser(request,id));
        Assert.assertEquals("User with id " + id + " is not presented in database", e.getMessage());
    }

    @Test
    public void updateUser_manyRoles() {
        List<String> roles = Stream.of(null, "admin", "user", "fakeRole")
                .collect(Collectors.toCollection(
                        ArrayList::new));
        Long id = 1L;
        Optional<AppUser> user = Optional.of(new AppUser(
                1L,
                "aaa",
                "aaa",
                "aaa@aaa.aaa",
                encoder.encode("correct_password"),
                ROLE_ADMIN));
        AdminSignupRequest request = new AdminSignupRequest(
                "bbbbbb",
                "bbbbbb",
                "bbb@bbb.bbb",
                "bbbbbb",
                null);
        Mockito.doReturn(user).when(userRepo).findById(id);
        roles.forEach(role -> {
            request.setRole(role);
            try {
                ResponseEntity<?> res = authController.updateUser(request,id);
                Assert.assertEquals(HttpStatus.OK, res.getStatusCode());
                Assert.assertEquals(new MessageResponse("User updated successfully!"),res.getBody());
            } catch(Exception e) {
                if (!role.equals("fakeRole")){
                    Assert.fail("Не ожидалось исключения: "+e.getMessage());
                }
                Assert.assertEquals("No such role: " + role, e.getMessage());
            }
        });
    }
}