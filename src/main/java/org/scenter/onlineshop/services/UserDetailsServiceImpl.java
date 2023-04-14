package org.scenter.onlineshop.services;

import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.repo.UserRepo;
import org.scenter.onlineshop.responses.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

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
        AppUser user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email \"" + email + "\" not found in the database")
                );
        log.info("User found in the database: {}",email);
        return UserDetailsImpl.build(user);
    }

    public ResponseEntity<?> deleteAppUser(Long userId){
        if (!userRepo.existsById(userId)){
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User with id "+userId+" is not presented in datatbase"));
        }
        deleteUser(userId);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    @Transactional
    public void saveUser(AppUser user){
        userRepo.save(user);
    }

    @Transactional
    public List<AppUser> getAllUsers() {
        return userRepo.findAll();
    }

    @Transactional
    public void deleteUser(Long userId){
        userRepo.deleteById(userId);
    }

}