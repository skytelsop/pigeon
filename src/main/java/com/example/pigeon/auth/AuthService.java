package com.example.pigeon.auth;

import com.example.pigeon.config.JwtService;
import com.example.pigeon.entity.Role;
import com.example.pigeon.entity.User;
import com.example.pigeon.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
  private final UserRepository repository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthResponse register(RegisterRequest request) {
    var user = User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .companyName(request.getCompanyName())
            .companyType(request.getCompanyType())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .referenceName(request.getReferenceName())
            .postalCode(request.getPostalCode())
            .streetAddress(request.getStreetAddress())
            .state(request.getState())
            .city(request.getCity())
            .country(request.getCountry())
            .role(Role.USER)
            .build();

    repository.save(user);
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder().token(jwtToken).build();
  }

  public AuthResponse authenticate(AuthRequest request) {
    authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
    );

    var user = repository.findByEmail(request.getEmail()).orElseThrow();
    var jwtToken = jwtService.generateToken(user);
    return AuthResponse.builder().token(jwtToken).build();
  }
}
