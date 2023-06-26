package com.code.controller;

import java.security.Principal;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.code.dao.UserRepo;
import com.code.entity.User;
import com.code.helper.Message;
import com.code.service.EmailService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepo userRepo;
	
	@GetMapping("/")
	public String homePage(Model model) {
		model.addAttribute("title","Home-Contact Manager");
		return "home";
	}
	@GetMapping("/about")
	public String aboutPage(Model model) {
		model.addAttribute("title","About-Contact Manager");
		return "about";
	}
	
	@GetMapping("/signup")
	public String signupPage(Model model) {
		model.addAttribute("title","Register-Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	//signup handler
	@PostMapping("/regi")
	public String signupHandle(@Valid @ModelAttribute("user")User user,BindingResult br,@RequestParam(value="agreement",defaultValue="false")boolean agreement, Model m,HttpSession session) {
		try {
			if(!agreement) {
				throw new Exception("You have not agreed to terms and conditions");
			}
			if(br.hasErrors()) {
			    System.out.println(br.toString());
				m.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println(user);
			
			User result = this.userRepo.save(user);
			m.addAttribute("user",new User());
			m.addAttribute("msg", new Message("Register Successful", "alert-success"));
			session.removeAttribute("msg");
			return "signup";
		}catch (Exception e) {
			e.printStackTrace();
			m.addAttribute("user",user);
			m.addAttribute("msg", new Message(e.getMessage(), "alert-danger"));
			session.removeAttribute("msg");
			return "signup";
		}
		
	}
	
	@GetMapping("/signin")
	public String login(Model m) {
		m.addAttribute("title","Login Page");
		return "login";
	}
	
	//forgetpassword
		@GetMapping("/forget")
		public String forgetpassword(Model m) {
			m.addAttribute("title","Forget?");
			return "forget";
		}
		
		//forget password handler
		@PostMapping("/send-otp")
		public String sendOTP(@RequestParam("email")String email,HttpSession session) {
			
			//generating OTP
			
			int otp = random.nextInt(9999);
			System.out.println(otp);
			
			//send otp to email
			String subject="OTP from CONTACT MANAGER";
			String message="<div style='border:1px solid; padding:20px'"
					      +"<h1>"
					      +"OTP is "
					      +"<b>"+otp+"</b></h1></div>";
			String to=email;
			boolean flag = this.emailService.sendEmail(subject, message, to);
			
			if(flag) {
				
				session.setAttribute("otp", otp);
				session.setAttribute("email", email);
				session.setAttribute("msg", new Message("We have successfully send OTP to your email", "success"));
				return "verify_otp";
			}else {
				session.setAttribute("msg", new Message("Something went wrong! Try again!", "danger"));
				return "forget";
			}
			
		}
		
		
		//verify otp
		@PostMapping("/verify-otp")
		public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
			
			int myOtp=(int) session.getAttribute("otp"); 
			String email=(String) session.getAttribute("email");
			if(myOtp==otp) {
				User user = this.userRepo.getUserByUserName(email);
				if(user==null) {
					//error msg
					session.setAttribute("msg", new Message("User not exists with this email", "danger"));
					return "forget";
				}else {
					//change password form
				}
				return "change_password";
			}else {
				
				session.setAttribute("msg", new Message("you have entered wrong OTP","danger"));
				return "verify_otp";
			}
		}
		
		//change password
		@PostMapping("/change-password")
		public String changepassword(@RequestParam("pass")String pass,
				                     @RequestParam("password")String password, 
				                     Principal principal, HttpSession session) {
			String email = (String) session.getAttribute("email");
			User User = this.userRepo.getUserByUserName(email);
			if(pass.equals(password)) {
				User.setPassword(this.passwordEncoder.encode(password));
				this.userRepo.save(User);
				session.setAttribute("msg", new Message("Your password changed successfully", "success"));
			}else {
				session.setAttribute("msg", new Message("Please enter same password in both fields", "danger"));
	            return "change_password";
			}
			return "redirect:/signin?change=Password changed successfully";
		}
}
