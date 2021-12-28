package com.jwtapp.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwtapp.demo.dao.DataRepository;
import com.jwtapp.demo.dao.UserRepository;
import com.jwtapp.demo.entity.Data;
import com.jwtapp.demo.entity.User;

@RestController
@RequestMapping("/staff")
public class StaffController {
	
	@Autowired DataRepository datarepo;
	
	@Autowired
	UserRepository userRepo;
	@RequestMapping("/greet")
	public String hello()
	{
		return "Hello staff";
	}
	
	@RequestMapping("/getAll")
	public List<Data> getAll()
	{
		return datarepo.findAll();
	}
	
	@RequestMapping("/getVerified")
	public List<Data> getVerified()
	{
		return datarepo.findByStatus(true);
	}
	
	@RequestMapping("/getNonVerified")
	public List<Data> getNonVerified()
	{
		return datarepo.findByStatus(false);
	}
	
	@RequestMapping("/getByUsername")
	public List<Data> getByUsername(@RequestBody User user)
	{
		User us= userRepo.findByUsername(user.getUsername());
		return us.getData();
	}
	
	@RequestMapping("/changeStatus{filename}")
	public void changeStatus( @PathVariable String filename)
	{
		Data data =datarepo.findByFilename(filename);
		data.setStatus(true);
		datarepo.save(data);
		
	}
}
