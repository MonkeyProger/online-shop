package org.scenter.onlineshop.unit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.scenter.onlineshop.BaseTest;
import org.scenter.onlineshop.common.exception.ElementIsPresentedException;
import org.scenter.onlineshop.common.requests.AdminSignupRequest;
import org.scenter.onlineshop.common.requests.SignupRequest;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.ERole;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.service.services.UserDetailsServiceImpl;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest extends BaseTest {
    @Spy
    UserRepo userRepo;

    @InjectMocks
    UserDetailsServiceImpl userDetailsService;

    @BeforeAll
    public static void initRoles() {

    }

    @Test
    void findById_userNotFound() {
        Long id = 0L;
        when(userRepo.findById(id))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.getUserById(id));
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.updateUser(new AdminSignupRequest(), id));
        verify(userRepo, Mockito.times(0)).save(any(AppUser.class));
    }

    @Test
    void findByEmail_userNotFound() {
        String email = "";
        when(userRepo.findByEmail(email))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class,
                () -> userDetailsService.getUserByEmail(email));
        verify(userRepo, Mockito.times(0)).save(any(AppUser.class));
    }

    @Test
    void registerUser_alreadyRegistered_Exception() {
        String email = "";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        when(userDetailsService.existsByEmail(email))
                .thenReturn(true);
        assertThrows(ElementIsPresentedException.class,
                () -> userDetailsService.registerUser(signupRequest));
        verify(userRepo, Mockito.times(0)).save(any(AppUser.class));
    }

    @Test
    void registerUser_AdminRequest_Success() {
        String email = "";
        AdminSignupRequest signupRequest = new AdminSignupRequest();
        signupRequest.setEmail(email);
        signupRequest.setRole("admin");
        when(userDetailsService.existsByEmail(email))
                .thenReturn(false);
        try {
            AppUser user = userDetailsService.registerUser(signupRequest);
            verify(userRepo, Mockito.times(1)).save(any(AppUser.class));
        } catch (Exception e) {
            fail("Test failed: No exception is expected: " + e);
        }
    }

    @Test
    void registerUser_defaultRequest_Success() {
        String email = "";
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail(email);
        when(userDetailsService.existsByEmail(email))
                .thenReturn(false);
        try {
            userDetailsService.registerUser(signupRequest);
            verify(userRepo, Mockito.times(1)).save(any(AppUser.class));

        } catch (Exception e) {
            fail("Test failed: No exception is expected: " + e);
        }
    }

    @Test
    void updateUser_Success() {
        AppUser oldUser = new AppUser();
        oldUser.setName("OldUser");
        oldUser.setSurname("OldUser");
        oldUser.setPassword("OldUser");
        oldUser.setEmail("OldUser");
        oldUser.setId(0L);
        oldUser.setRole(ERole.ROLE_USER);

        AdminSignupRequest signupRequest = new AdminSignupRequest();
        signupRequest.setEmail("NewUser");
        signupRequest.setRole("admin");
        signupRequest.setPassword("NewUser");
        signupRequest.setName("NewUser");
        signupRequest.setSurname("NewUser");

        Optional<AppUser> optional = Optional.of(oldUser);

        Long id = 0L;
        doReturn(optional).when(userRepo).findById(id);
        try {
            userDetailsService.updateUser(signupRequest, id);
            verify(userRepo, Mockito.times(1)).save(any(AppUser.class));

        } catch (Exception e) {
            fail("Test failed: No exception is expected: " + e);
        }
    }

    @Test
    void deleteUser_NoSuchUser_Exception() {
        Long id = 0L;
        doReturn(false).when(userRepo).existsById(id);
        assertThrows(NoSuchElementException.class, () -> userDetailsService.deleteAppUser(id));
        verify(userRepo, Mockito.times(0)).deleteById(id);
    }

    @Test
    void deleteUser_Success() {
        Long id = 0L;
        doReturn(true).when(userRepo).existsById(id);
        userDetailsService.deleteAppUser(id);
        verify(userRepo, Mockito.times(1)).deleteById(id);
    }

}
