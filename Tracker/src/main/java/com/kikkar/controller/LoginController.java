package com.kikkar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kikkar.model.User;
import com.kikkar.service.UserManager;

@Controller
public class LoginController {

	@Autowired
	private UserManager userManager;
	
	
	@RequestMapping(value="/login")
	public String login(@RequestParam(value="error", required=false) String error,
						@RequestParam(value="logout", required=false) String logout, Model model) {
		
		if(error != null) {
			model.addAttribute("error", "Invalid username or password");
		}
		else if(logout != null) {
			model.addAttribute("logout", "You have been logged out successfully");
		}
		
		model.addAttribute("user", new User());
		
		return "login";
	}

	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String registerUser(@ModelAttribute("user") User user) {
		if(user != null) {
			userManager.addUser(user);
		}
		return "login";
	}

}
