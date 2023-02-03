package org.scenter.onlineshop.services;

import lombok.extern.slf4j.Slf4j;
import org.scenter.onlineshop.domain.AppUser;
import org.scenter.onlineshop.domain.Role;
import org.scenter.onlineshop.repo.RoleRepo;
import org.scenter.onlineshop.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepo userRepository;
    @Autowired
    RoleRepo roleRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email \"" + email + "\" not found in the database")
                );
        log.info("User found in the database: {}",email);
        return UserDetailsImpl.build(user);
    }

    @Transactional
    public void saveRole(Role role){
        roleRepository.save(role);
    }

    @Transactional
    public void saveUser(AppUser user){
        userRepository.save(user);
    }
}