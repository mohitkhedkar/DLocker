package com.jwtapp.demo;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtapp.demo.dao.UserRepository;
import com.jwtapp.demo.entity.User;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	UserRepository repo;
	
	@RequestMapping("/changeRole")
	public String changeRole(@RequestBody User user)
	{
		repo.setRole(user.getUsername(),"ROLE_staff");
		return "done";
	}
	
	@RequestMapping("/deleteUser")
	public String deleteUser(@RequestBody User user)
	{
			repo.setStatus(user.getUsername(), false);
			return "user deleted";
	}
}
