package com.example.pigeon.config;

import com.example.pigeon.repository.RoleRepository;
import com.example.pigeon.repository.UserRepository;
import com.example.pigeon.security.CustomRememberMeServices;
import com.example.pigeon.security.DifferentLocationChecker;
import com.example.pigeon.security.google2fa.CustomAuthenticationProvider;
import com.example.pigeon.security.google2fa.CustomWebAuthenticationDetailsSource;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import java.io.File;
import java.io.IOException;

@Configuration
@ComponentScan(basePackages = {"com.example.pigeon.security"})
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private static final String maxmind = "src/main/resources/maxmind/GeoLite2-Country.mmdb";

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private AuthenticationSuccessHandler authenticationSuccessHandler;

  @Autowired
  private LogoutSuccessHandler logoutSuccessHandler;

  @Autowired
  private AuthenticationFailureHandler authenticationFailureHandler;

  @Autowired
  private CustomWebAuthenticationDetailsSource authenticationDetailsSource;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private final RoleRepository roleRepository;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.
            authorizeRequests()
            .antMatchers("/login*", "/logout*", "/signin/**", "/signup/**", "/customLogin",
                    "/user/registration*", "/registrationConfirm*", "/expiredAccount*",
                    "/registration*", "/badUser*", "/user/resendRegistrationToken*", "/forgetPassword*",
                    "/user/resetPassword*", "/user/savePassword*", "/updatePassword*", "/user/changePassword*",
                    "/emailError*", "/resources/**", "/successRegister*", "/qrcode*", "/user/enableNewLoc*")

            .permitAll()
            .antMatchers("/invalidSession*")
            .anonymous()
            .antMatchers("/user/updatePassword*")
            .hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
            .anyRequest()
            .hasAuthority("READ_PRIVILEGE")
            .and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/homepage.html")
            .failureUrl("/login?error=true")
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .authenticationDetailsSource(authenticationDetailsSource)
            .permitAll()
            .and()
            .sessionManagement()
            .invalidSessionUrl("/invalidSession.html")
            .maximumSessions(1)
            .sessionRegistry(sessionRegistry())
            .and()
            .sessionFixation()
            .none()
            .and()
            .logout()
            .logoutSuccessHandler(logoutSuccessHandler)
            .invalidateHttpSession(true)
            .logoutSuccessUrl("/logout.html?logSucc=true")
            .deleteCookies("JSESSIONID")
            .permitAll()
            .and()
            .rememberMe()
            .rememberMeServices(rememberMeServices())
            .key("theKey");

    return http.build();
  }

  @Bean
  public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {

    return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(authenticationProvider())
            .build();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {

    final CustomAuthenticationProvider authenticationProvider = new CustomAuthenticationProvider();

    authenticationProvider.setUserDetailsService(userDetailsService);
    authenticationProvider.setPasswordEncoder(encoder());
    authenticationProvider.setPostAuthenticationChecks(differentLocationChecker());

    return authenticationProvider;
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {

    return (web) -> web.ignoring().antMatchers("/resources/**", "/h2/**");
  }

  @Bean
  public PasswordEncoder encoder() { return new BCryptPasswordEncoder(22); }

  @Bean
  public SessionRegistry sessionRegistry() { return new SessionRegistryImpl(); }

  @Bean
  public RememberMeServices rememberMeServices() {

    CustomRememberMeServices rememberMeServices = new CustomRememberMeServices("theKey",
            userDetailsService,
            new InMemoryTokenRepositoryImpl());

    return rememberMeServices;
  }

  @Bean(name = "GeoIPCountry")
  public DatabaseReader databaseReader() throws IOException, GeoIp2Exception {

    final File resource = new File(maxmind);
    return new DatabaseReader.Builder(resource).build();
  }

  public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {

    DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy());

    return expressionHandler;
  }

  @Bean
  public RoleHierarchy roleHierarchy() {

    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    String hierarchy = "ROLE_ADMIN > ROLE_USER";
    roleHierarchy.setHierarchy(hierarchy);

    return roleHierarchy;
  }

  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() { return new HttpSessionEventPublisher(); }

  @Bean
  public DifferentLocationChecker differentLocationChecker() { return new DifferentLocationChecker(); }
}
