package com.example.pigeon.config;

import com.example.pigeon.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

  @Bean
  public ActiveUserStore activeUserStore() { return new ActiveUserStore(); }
}
