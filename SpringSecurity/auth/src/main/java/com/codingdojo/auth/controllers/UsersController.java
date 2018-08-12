package com.codingdojo.auth.controllers;

import java.security.Principal;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.codingdojo.auth.models.User;
import com.codingdojo.auth.services.UserService;
import com.codingdojo.auth.validators.UserValidator;

@Controller
public class UsersController {
	
	private UserService userServ;
	private UserValidator userVal;
	
	public UsersController(UserService userServ, UserValidator userVal) {
		this.userServ = userServ;
		this.userVal = userVal;
	}
	
	@RequestMapping("/registration")
    public String registerForm(@Valid @ModelAttribute("user") User user) {
        return "registrationPage.jsp";
    }
	
	@PostMapping("/registration")
	public String registration(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, HttpSession session) {
        userVal.validate(user, result);
		if (result.hasErrors()) {
            return "registrationPage.jsp";
        }
        userServ.saveUserWithAdminRole(user);
        return "redirect:/login";
    }
    
    @RequestMapping("/login")
    public String login(@RequestParam(value="error", required=false) String error, @RequestParam(value="logout", required=false) String logout, Model model) {
        if (error != null) model.addAttribute("errorMessage", "Invalid Credentials, Please try again.");
        if (logout != null) model.addAttribute("logoutMessage", "Logout Successful!");
    	return "loginPage.jsp";
    }
    
    @RequestMapping("/admin")
	public String adminPage(Principal principal, Model model) {
		String username = principal.getName();
		model.addAttribute("currentAdmin", userServ.findByUsername(username));
		return "adminPage.jsp";
	}
    
    @RequestMapping(value= {"/", "/home"})
    public String home(Principal principal, Model model) {
    	String username = principal.getName();
    	model.addAttribute("currentUser", userServ.findByUsername(username));
    	return "homePage.jsp";
    }
    
}
