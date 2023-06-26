package com.code.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class MyConfig{

	@Bean
	public UserDetailsService getUserDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider dap=new DaoAuthenticationProvider();
		dap.setUserDetailsService(this.getUserDetailsService());
		dap.setPasswordEncoder(passwordEncoder());
		return dap;
	}

	
	
	//configure method
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
		http
		    .authorizeHttpRequests()
		    .requestMatchers("/admin/**").hasRole("ADMIN")
		    .requestMatchers("/user/**").hasRole("USER")
		    .requestMatchers("/**").permitAll().and()
		    .formLogin().loginPage("/signin")
		    .loginProcessingUrl("/dologin")
		    .defaultSuccessUrl("/user/index")
		    .and().csrf().disable();
		http.authenticationProvider(authenticationProvider());
		return http.build();
	}
}
