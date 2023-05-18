package com.smartgridz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * The password encoding we will be using for the product. This will be used in the
     * UserServiceImpl to encode the users passwords and be used below to configure
     * the UserDetailsService (CustomUserDetailsService) to use the same encoder.
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.authorizeHttpRequests((authorize) ->
                        authorize.requestMatchers("/register/**").hasRole("ADMIN")
                                .requestMatchers("/config_email/**").hasRole("ADMIN")
                                .requestMatchers("/users").hasRole("ADMIN")
                                .requestMatchers("/forgot_password/**").permitAll()
                                .requestMatchers("/reset_password/**").permitAll()
                                .requestMatchers("/message/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .requestMatchers("/images/**").permitAll()

                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/users", true)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
        return http.build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }
}
