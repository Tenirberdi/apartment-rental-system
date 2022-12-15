package com.example.system.Security;

import com.example.system.ErrorHandlers.ExceptionResolver;
import com.example.system.ErrorHandlers.CustomAuthenticationFailureHandler;
import com.example.system.ErrorHandlers.RestAccessDeniedHandler;
import com.example.system.ErrorHandlers.RestAuthenticationEntryPoint;
import com.example.system.Security.Jwt.JwtConfigurer;
import com.example.system.Security.Jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import static com.example.system.EndPoints.URLs.*;
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String ADMIN_ENDPOINT = ADMIN_BASE_URL + "/**";
    private static final String AUTHENTICATION_ENDPOINT = AUTHENTICATION_BASE_URL + "/**";
    private static final String USER_ENDPOINT =  USER_BASE_URL + "/**";
    private static final String MEDIA_ENDPOINT = MEDIA_BASE_URL + "/**";
    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    @Qualifier("restAuthenticationEntryPoint")
    RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Autowired
    @Qualifier("restAccessDeniedHandler")
    RestAccessDeniedHandler restAccessDeniedHandler;

    @Autowired
    @Qualifier("exceptionResolver")
    ExceptionResolver exceptionResolver;

    @Autowired
    @Qualifier("customAuthenticationFailureHandler")
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    // Create 2 users for demo
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTHENTICATION_ENDPOINT, MEDIA_ENDPOINT).permitAll()
                .antMatchers(ADMIN_ENDPOINT ).hasRole("ADMIN")
                .antMatchers(USER_ENDPOINT).hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider))
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint);

    }
    @Bean
    public CustomAuthenticationFailureHandler authenticationHandlerBean() {
        return new CustomAuthenticationFailureHandler();
    }


    @Autowired
    void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }


}
