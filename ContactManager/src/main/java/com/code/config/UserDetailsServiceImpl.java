package com.code.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.code.dao.UserRepo;
import com.code.entity.User;

public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetching user from database
		User user = userRepo.getUserByUserName(username);
		if(user==null) {
			throw new UsernameNotFoundException("Could not found user");
		}
		CustomUserDetails cud=new CustomUserDetails(user);
		
		return cud;
	}

}
