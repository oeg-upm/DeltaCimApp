package cim.controller;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cim.model.User;
import cim.service.LoginService;

/**
 * 
 * TODO: Logout in all templates
 * @author jcano
 *
 */

@Controller
public class LoginController extends AbstractController{
	
	@Autowired
	public LoginService conn;
	
	// Provide GUI
	
	@RequestMapping(value="/", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String initialGUI(Model model) {
		if(!isLogged()) {
			return "redirect:/login";
			
		}
		return "redirect:/index.html";
	}
	
	@RequestMapping(value="/login", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getLoginService(Model model) {
		if(isLogged()) {
			return "redirect:/index.html";
		}
		User user = new User();
		user.setUsername("");
		user.setPassword("");
		model.addAttribute("user", user);
		return "login";
	}
	
	
	
	@RequestMapping(value="/api/login", method = RequestMethod.POST)
	public String connect(@Valid @ModelAttribute(value="user") User user, BindingResult bindingResult, HttpServletResponse response, Model model) {
		prepareResponse(response);
		if(!bindingResult.hasErrors()) {	
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
//			System.out.println(user.getUsername());
//			System.out.println(user.getPassword());
			try {
				conn.connect(user.getUsername(), user.getPassword());
			}catch (Exception e){
				e.printStackTrace();
				return "redirect:/login";
			}
		}
		if(conn.isLogged()) {
			return "redirect:/bridging";
		}else {
			return "redirect:/login";
		}
	}
	

}
