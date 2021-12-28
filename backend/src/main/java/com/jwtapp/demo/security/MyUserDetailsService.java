package com.jwtapp.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jwtapp.demo.dao.UserRepository;
import com.jwtapp.demo.entity.User;

@Service
public class MyUserDetailsService implements UserDetailsService{

	@Autowired
	public UserRepository repo;
		
	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		User user=repo.findByUsername(userName);
		if(user==null)
			throw new UsernameNotFoundException("User 404");
		return new UserPrincipal(user);
		//return new User(user.getUsername(),user.getPassword(),new ArrayList<>());
	}

}
