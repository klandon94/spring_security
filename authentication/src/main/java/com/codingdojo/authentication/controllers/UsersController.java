package com.codingdojo.authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.codingdojo.authentication.models.User;
import com.codingdojo.authentication.services.UserService;
import com.codingdojo.authentication.validator.UserValidator;

@Controller
public class UsersController {
	private final UserService userServ;
	private final UserValidator userVal;
	
	public UsersController(UserService userServ, UserValidator userVal) {
		this.userServ = userServ;
		this.userVal = userVal;
	}
	
	@RequestMapping("/registration")
	public String registerForm(@ModelAttribute("user") User user) {
		return "registrationPage.jsp";
	}
	@RequestMapping("/login")
	public String login() {
		return "loginPage.jsp";
	}
	
	@RequestMapping(value="/registration", method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result, HttpSession session) {
		userVal.validate(user, result);
		if (result.hasErrors()) return "registrationPage.jsp";
		else {
			User u = userServ.registerUser(user);
			session.setAttribute("userId", u.getId());
			return "redirect:/home";
		}
	}
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public String loginUser(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpSession session) {
		if (userServ.authenticateUser(email, password)) {
			User u = userServ.findByEmail(email);
			session.setAttribute("userId", u.getId());
			return "redirect:/home";
		}
		model.addAttribute("error", "Unable to login");
		return "loginPage.jsp";
	}
	
	@RequestMapping("/home")
	public String home(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute("userId");
		User u = userServ.findUserById(userId);
		model.addAttribute("user", u);
		return "homePage.jsp";
	}
	@RequestMapping("/logout")
	public String logout(HttpSession session, RedirectAttributes redir) {
		session.invalidate();
		redir.addFlashAttribute("success", "successfully logged out");
		return "redirect:/login";
	}
	
}
