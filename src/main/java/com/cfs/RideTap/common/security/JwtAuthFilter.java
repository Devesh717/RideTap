package com.cfs.RideTap.common.security;

import java.io.IOException;

import com.cfs.RideTap.common.util.JwtUtil;
import com.cfs.RideTap.user.service.implementations.UserServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  UserServicesImpl userDetailsServiceImpl;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getServletPath().contains("auth") ||
        request.getServletPath().contains("swagger-ui") ||
        request.getServletPath().contains("api-docs")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    final String token = header.split(" ")[1].trim();
    if (!jwtUtil.validateToken(token)) {
      filterChain.doFilter(request, response);
      return;
    }

    String email = jwtUtil.getUsernameFromJWT(token);

    UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(email);

    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
        null,
        userDetails.getAuthorities());

    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

    filterChain.doFilter(request, response);
  }
}
