package com.jwtapp.demo;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jwtapp.demo.dao.UserRepository;
import com.jwtapp.demo.entity.User;
import com.jwtapp.demo.jwt.AuthenticationRequest;
import com.jwtapp.demo.jwt.AuthenticationResponse;
import com.jwtapp.demo.jwt.JwtUtil;
import com.jwtapp.demo.security.MyUserDetailsService;
//Access only to localhost, strong pass, limit access to few hosts, change port and do not use root 
//@PreAuthorize("hasRole('user')")
@RestController
public class Controller {
//	@Autowired
//	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@Autowired
	private JavaMailSender javaMailSender;
	
	@Autowired
	private UserRepository repo;
	@RequestMapping("/")
	public String home()
	{
		return "Hello";	
	}

	@RequestMapping("/register")
	public String register(@RequestBody User us)
	{
		List<User> user= repo.findByEmail(us.getEmail());
		User uname=repo.findByUsername(us.getUsername());
		if(user.size()==0 && uname==null)
		{
			us.setPassword(BCrypt.hashpw(us.getPassword(), BCrypt.gensalt()));
			
			User u=new User( us.getUsername(), us.getPassword(), us.getEmail());
			repo.save(u);
			File f=new File("D:\\Users\\shrey\\Documents\\sts\\JwtApp\\src\\main\\resources\\static\\"+us.getUsername());
			boolean bool= f.mkdir();
			if(bool)
				System.out.println("created");
				
			return"Registered";
		}
		else if(user.size()>1)	
			return "email already used";
		else
			return "username already used";
	}
	@RequestMapping("/mailAuthenticate")
	public String sendMail( @RequestBody User u)
	{
		Random random=new Random();
		random.ints(0, 9);
		int otp=0;
		for (int i = 0; i < 6; i++)
			otp=otp*10+random.nextInt(9);
		repo.setOtp(otp, u.getEmail());
		SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(u.getEmail());
        msg.setSubject("Verify your email");
        msg.setText("Otp:"+otp);
        javaMailSender.send(msg);
		return "sentMail";
	}
	
	@RequestMapping("/otpVerify")
	public String otpVerify(@RequestBody User u)
	{
		List<User> us= repo.findByEmail(u.getEmail());
		if(us.size()==0)
			return "Invalid Email";
		if(u.getOtp()==us.get(0).getOtp())
		{
			repo.setAuthentication(u.getEmail());
			return "Email verified";
		}
		return "Wrong otp";
	}
	
	@RequestMapping("/changePassword")
	public String changePassword(@RequestBody User u)
	{
		Random random=new Random();
		random.ints(0, 9);
		int otp=0;
		for (int i = 0; i < 6; i++)
			otp=otp*10+random.nextInt(9);
//		SimpleDateFormat formatter= new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
		LocalDateTime n=LocalDateTime.now();
		repo.setExpiry(u.getEmail(), otp, n);
		SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(u.getEmail());
        msg.setSubject("Request for password change");
        msg.setText("Otp:"+otp+"\n Otp is valid only for an hour");
        javaMailSender.send(msg);
		return "sentMail";
		
	}
	@RequestMapping("/updatePassword")
	public String updatePassword(@RequestBody User us)
	{
		List<User> user= repo.findByEmail(us.getEmail());
		LocalDateTime n=LocalDateTime.now();
		LocalDateTime m=user.get(0).getExpiry();
		Duration duration = Duration.between(m, n);
		if(duration.getSeconds()>3600)
			return "Otp expired";
		us.setPassword(BCrypt.hashpw(us.getPassword(), BCrypt.gensalt()));
		repo.setPassword(us.getEmail(),us.getPassword());
		return "password updated"; 
	}
	
	@RequestMapping(value="/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?>  createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws  Exception{
		try 
		{
			User user=repo.findByUsername(authenticationRequest.getUsername());
			if(user==null || !BCrypt.checkpw(authenticationRequest.getPassword(), user.getPassword()))
				throw new BadCredentialsException("Incorrect username or password");
//			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
//					authenticationRequest.getUsername(), authenticationRequest.getPassword()));
		}catch (BadCredentialsException e) {
			throw new Exception("Incorrect username or password");
		}
		final UserDetails userDetails=userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		User us=repo.findByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);
		System.out.println(us.getAuthentication());
		if(!us.getStatus())
			return (ResponseEntity<?>) ResponseEntity.ok("Account blocked");
		if(us.getAuthentication()==0)
			return (ResponseEntity<?>) ResponseEntity.ok("Verify your email");
		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}
	
	
	
	
	
}
