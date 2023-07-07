package org.scenter.onlineshop.service.services;

import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.common.exception.ElementIsPresentedException;
import org.scenter.onlineshop.common.requests.SignupRequest;
import org.scenter.onlineshop.common.requests.AdminSignupRequest;
import org.scenter.onlineshop.common.responses.MessageResponse;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.ERole;
import org.scenter.onlineshop.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepo userRepo;

    @Autowired
    public UserDetailsServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = getUserByEmail(email);
        return UserDetailsImpl.build(user);
    }

    public boolean existsById(Long userId) {
        return userRepo.existsById(userId);
    }

    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    public AppUser getUserById(Long userId) {
        Optional<AppUser> user = userRepo.findById(userId);
        if (!user.isPresent()){
            log.error("User with id [{}] not found", userId);
            throw new NoSuchElementException("User with id \"" + userId + "\" was not found in the database");
        }
        AppUser foundUser = user.get();
        log.info("User [{}] with id [{}] found in the database!",
                foundUser.getEmail(), foundUser.getId());
        return foundUser;
    }

    public AppUser getUserByEmail(String email) {
        Optional<AppUser> user = userRepo.findByEmail(email);
        if (!user.isPresent()){
            log.error("User with email [{}] not found", email);
            throw new NoSuchElementException("User with email \"" + email + "\" was not found in the database");
        }
        AppUser foundUser = user.get();
        log.info("User [{}] with id [{}] found in the database!",
                foundUser.getEmail(), foundUser.getId());
        return foundUser;
    }

    @Transactional
    public AppUser registerUser(SignupRequest signUpRequest) throws ElementIsPresentedException {
        if (existsByEmail(signUpRequest.getEmail())) {
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
                signUpRequest.getPassword(),
                role);

        return saveUser(user);
    }

    @Transactional
    public AppUser updateUser(AdminSignupRequest signUpRequest, Long id) {
        AppUser user = getUserById(id);

        ERole role = getERole(signUpRequest.getRole());
        user.setRole(role);
        user.setName(signUpRequest.getName());
        user.setSurname(signUpRequest.getSurname());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());

        return saveUser(user);
    }

    public ResponseEntity<?> deleteAppUser(Long userId){
        if (!userRepo.existsById(userId)){
            throw new NoSuchElementException("User with id "+userId+" is not presented in database");
        }
        deleteUser(userId);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    public AppUser saveUser(AppUser user){
        return userRepo.save(user);
    }

    public List<AppUser> getAllUsers() {
        return userRepo.findAll();
    }

    private void deleteUser(Long userId){
        userRepo.deleteById(userId);
    }


    private ERole getERole(String role) throws NoSuchElementException{
        if (role == null) {
            return ERole.ROLE_USER;
        }
        switch (role) {
            case "admin":
                return ERole.ROLE_ADMIN;
            case "user":
                return ERole.ROLE_USER;
            default:
                throw new NoSuchElementException("No such role: " + role);
        }
    }
}