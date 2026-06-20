package com.cfs.RideTap.config;

import com.cfs.RideTap.enums.Role;
import com.cfs.RideTap.models.UserEntity;
import com.cfs.RideTap.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserEntity user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found by email " + email));

    return new User(user.getEmail(), user.getPassword(), mapRolesToAuthorities(user.getRoles()));
  }

  private Collection<GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
    return roles.stream().map(role -> new SimpleGrantedAuthority(role.toString())).collect(Collectors.toList());
  }



}
