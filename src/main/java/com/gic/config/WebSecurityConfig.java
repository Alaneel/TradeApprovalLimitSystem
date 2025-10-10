package com.gic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Basic security configuration.
 * In production, implement proper authentication and authorization.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Disable CSRF for API endpoints (enable in production with proper token handling)
            .authorizeRequests()
                .antMatchers("/actuator/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .antMatchers("/api/**").permitAll() // In production, add authentication here
                .anyRequest().authenticated()
            .and()
            .headers()
                .frameOptions().deny()
                .xssProtection().block(true)
                .and()
                .contentSecurityPolicy("default-src 'self'");
    }
}
