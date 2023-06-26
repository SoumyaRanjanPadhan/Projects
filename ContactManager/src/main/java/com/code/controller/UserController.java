package com.code.controller;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.code.dao.ContactRepo;
import com.code.dao.UserRepo;
import com.code.entity.Contact;
import com.code.entity.User;
import com.code.helper.Message;

import jakarta.servlet.http.HttpSession;





@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepo userRepo;
	@Autowired
	private ContactRepo contactRepo;
	
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String name = principal.getName();
		System.out.println(name);
		
		User user = userRepo.getUserByUserName(name);
		System.out.println(user);
		model.addAttribute("user",user);
	}

	
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title","User-Dashboard");
		return "user/user_dashboard";
	}
	
	@GetMapping("/add-contact")
	public String addContactForm(Model model) {
		model.addAttribute("title","Add-Contact");
		model.addAttribute("contact", new Contact());
		return "user/add_contact_form";
	}
	
	
	@PostMapping("/process-contact")
	public String processContactForm(@ModelAttribute("contact") Contact contact, BindingResult res,
			                         @RequestParam("image") MultipartFile imagefile,
			                         Principal principal, HttpSession session) {
		try {
			if(!imagefile.isEmpty()) {
				contact.setImage(imagefile.getOriginalFilename());
				
				//uploading file to folder/server
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath()+File.separator+imagefile.getOriginalFilename());
				Files.copy(imagefile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded");
		}
		
		System.out.println(contact);
		
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);
		contact.setUser(user);
		user.getContacts().add(contact);
		
		//save to database
		this.userRepo.save(user);
		
		//success msg
		session.setAttribute("msg", new Message("Contact Added Successfully", "success"));
		
		}catch (Exception e) {
			e.printStackTrace();
		//error msg
	    session.setAttribute("msg", new Message("Something Went Wrong!!!", "danger"));
	    
		}
		return "user/add_contact_form";
	}
	
	
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page")int page, Model m, Principal principal) {
		m.addAttribute("title","All-Contacts");
		String name = principal.getName();
		User user = this.userRepo.getUserByUserName(name);
		Pageable pageable = PageRequest.of(page, 4);
		Page<Contact> contacts = this.contactRepo.findContactByUser(user.getId(),pageable);
	    m.addAttribute("contacts", contacts);
	    m.addAttribute("currentpage",page);
	    m.addAttribute("totalpage", contacts.getTotalPages());
		return "user/show_contacts";
	}
	
	
	
	//showing particular contact
	@GetMapping("/contact/{id}")
	public String showContactDetail(@PathVariable("id")int id, Model m) {
		Optional<Contact> contact = this.contactRepo.findById(id);
		Contact contact2 = contact.get();
		m.addAttribute("contact", contact2);
		
		return "user/contact_detail";
	}
	
	
	//delete contact
	@GetMapping("/delete/{id}")
	public String deleteContact(@PathVariable("id")int id,Principal principal) {
		Contact contact = this.contactRepo.findById(id).get();
		User user = this.userRepo.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepo.save(user);
		
		return "redirect:/user/show-contacts/0";
	}
	
	//update contact
	@PostMapping("/update-contact/{id}")
	public String updateContact(@PathVariable("id")int id,Model m) {
		m.addAttribute("title","Update-Contact");
		Contact contact = this.contactRepo.findById(id).get();
		m.addAttribute("contact",contact);
		return "user/update_contact";
	}
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateContactHandler(@ModelAttribute Contact contact,BindingResult res,
			                           @RequestParam("image") MultipartFile imagefile,
			                           Model m, HttpSession session, Principal principal) {
		try {
			//old contact details
			Contact oldContact = this.contactRepo.findById(contact.getId()).get();
			if(!imagefile.isEmpty()) {
				
				//delete old photo
				File delfile = new ClassPathResource("static/img").getFile();
				File file1=new File(delfile,oldContact.getImage());
				file1.delete();

				//update photo
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath()+File.separator+imagefile.getOriginalFilename());
				Files.copy(imagefile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(imagefile.getOriginalFilename());
			}else {
				contact.setImage(oldContact.getImage());
			}
			User user = this.userRepo.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepo.save(contact);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return "redirect:/user/show-contacts/0";
	}
	
	
	//profile details
	@GetMapping("/profile")
	public String yourProfile(Model m) {
		m.addAttribute("title","Profile");
		return "user/profile";
	}
	
	//update profile
	@PostMapping("update-profile/{id}")
	public String updateProfile(@PathVariable("id")int id, Model m) {
		m.addAttribute("title","Update-Profile");
		User user = this.userRepo.findById(id).get();
		m.addAttribute("user",user);
		return "user/update_profile";
	}
	
	//update profile handler
	@PostMapping("/process-profile")
	public String processProfile(@ModelAttribute User user,BindingResult res,
                                 @RequestParam("image") MultipartFile imagefile,
                                 Model m, HttpSession session, Principal principal) {
		try {
			if(!imagefile.isEmpty()) {
				File file = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(file.getAbsolutePath()+File.separator+imagefile.getOriginalFilename());
				Files.copy(imagefile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(imagefile.getOriginalFilename());
			}
			
			this.userRepo.save(user);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/user/profile";
	}
	
	//setting handler
	@GetMapping("/settings")
	public String openSettings(Model m) {
		m.addAttribute("title","Settings");
		return "user/settings";
	}
	
	
	//change password
	@PostMapping("/change-password")
	public String changepassword(@RequestParam("oldpass")String oldpassword,
			                     @RequestParam("newpass")String newpassword, 
			                     Principal principal, HttpSession session) {
		String name = principal.getName();
		User currentUser = this.userRepo.getUserByUserName(name);
		if(this.bCryptPasswordEncoder.matches(oldpassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepo.save(currentUser);
			session.setAttribute("msg", new Message("Your password changed successfully", "success"));
		}else {
			session.setAttribute("msg", new Message("Please enter valid Password !!", "danger"));
            return "redirect:/user/settings";
		}
		return "redirect:/signin?change=Password changed successfully";
	}
	
	
	
}
