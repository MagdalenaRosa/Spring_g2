package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configurers.userdetails.DaoAuthenticationConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.services.UserService;

import lombok.RequiredArgsConstructor;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    final UserService userService;
    final AuthPasswordConfig authPasswordConfig;
    
    // @Bean
    // public TestBean testBean(){
    //     return new TestBean();
    // }
    private static final String[] PUBLIC_URI={"/","/product","/categories","/contact","/productDetail/{id}","/categoryDetails/{id}","/user/add","/user/save"};

    // @Bean
    // public InMemoryUserDetailsManager inMemoryUserDetailsManager(){
    //     UserDetails user1 = User.withUsername("user1")
    //     .password("{noop}haslo1234")
    //     .roles("USER")
    //     .build();
    //     UserDetails user2 = User.withUsername("user2")
    //     .password(passwordEncoder().encode("haslo1234"))
    //     .roles("USER")
    //     .build();
    //     UserDetails admin = User.withUsername("admin")
    //     .password("haslo1234")
    //     .roles("ADMIN")
    //     .build();
    //     return new InMemoryUserDetailsManager(user1,user2, admin);
    // }

    @Bean 
    DaoAuthenticationProvider authenticationProvider(){

        System.out.println(authPasswordConfig.passwordEncoder().encode("Silverhand1234!"));

        DaoAuthenticationProvider authenticationProvider= new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(
            email -> userService.findUserByEmail(email)
            .map(user -> User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole())
                .build()
            ).orElseThrow(() ->  new UsernameNotFoundException(""))
        );
        authenticationProvider.setPasswordEncoder(authPasswordConfig.passwordEncoder());
        return authenticationProvider;

    }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
        .formLogin(login-> login.loginPage("/login").defaultSuccessUrl("/").permitAll())
        .logout(logout-> logout.logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .permitAll()
        )   
        .authorizeHttpRequests((auth)-> auth.requestMatchers(PUBLIC_URI).permitAll()
            .anyRequest().authenticated())
        .build();


    }

    

}
