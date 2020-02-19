package cim.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.model.User;
import cim.service.UserService;

@Controller
public class LocalUsersController  extends AbstractController{
	
	@Autowired
	public UserService userService;
	
	
	@RequestMapping(value="/users", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getDashboard(Model model, HttpServletResponse response) {
		prepareResponse(response);
		model.addAttribute("users", userService.getAllUsers());
		User user = new User();
		user.setUsername("");
		user.setPassword("");
		user.setAuthority(null);
		model.addAttribute("user", user);
		return "users.html";
	}


	@RequestMapping(value="/api/cimusers", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<User> getAllAcl(HttpServletResponse response) {
		prepareResponse(response);
		List<User> cimusers =  new ArrayList<>();
		cimusers = userService.getAllUsers();
		response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		// By default returns the error code
		return cimusers;
	}

	@RequestMapping(value="/api/cimuser", method = RequestMethod.POST)
	public String saveCimUser(@Valid @ModelAttribute(value="user") User user, BindingResult bindingResult, HttpServletResponse response, Model model) {
		prepareResponse(response);
		if(!bindingResult.hasErrors()) {	
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			userService.createUser(user);
		}else {
			System.out.println(bindingResult.getAllErrors());
		}
		return "redirect:/users";
	}

	@RequestMapping(value="/api/cimuser", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteCimUser(@RequestBody(required=true) String userId, HttpServletResponse response, Model model) {
		prepareResponse(response);		
		if(!userId.isEmpty() ) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			userService.remove(userId);
		}

	}

}
