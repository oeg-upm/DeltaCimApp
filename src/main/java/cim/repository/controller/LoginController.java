package cim.repository.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.repository.model.Route;
import cim.repository.model.User;
import cim.repository.service.LoginService;

@Controller
public class LoginController {
	
	public LoginService conn;

	// Provide GUI
	
	@RequestMapping(value="/login", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getLoginService(Model model) {
		User user = new User();
		user.setUsername("");
		user.setPassword("");
		model.addAttribute("user", user);
		return "login.html";
	}
	
	
	
	@RequestMapping(value="/api/login", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public void connect(String username, String password) {
		conn.connect(username, password);
	}

}
