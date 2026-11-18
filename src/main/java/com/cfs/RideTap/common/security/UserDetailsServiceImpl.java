package com.cfs.RideTap.common.security;

import com.cfs.RideTap.user.enums.Role;
import com.cfs.RideTap.user.model.UserEntity;
import com.cfs.RideTap.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

  private List<GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
    if (roles == null) {
      return Collections.emptyList();
    }
    return roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.name()))
            .collect(Collectors.toList());
  }
}
