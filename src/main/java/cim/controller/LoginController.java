package cim.controller;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import cim.model.User;
import cim.service.UserService;

/**
 * 
 * TODO: Logout in all templates
 * @author jcano
 *
 */

@Controller
public class LoginController extends AbstractController{

	@Autowired
	public UserService userService;
	
	@PostConstruct
	public void initUsers() {
		userService.createDefaultUser();
	}
	
	// Provide GUI

	@RequestMapping(value= {"/","/api/login"}, method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String initialGUI(Model model) {
		return "redirect:/login";
	}

	@RequestMapping(value="/login", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getLoginService(Model model) {
		//Avoid logged users to navigate into the login page
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if(auth.getPrincipal() instanceof UserDetails){
			return "redirect:/dashboard";
		}
		//Creates a new user
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
			try {
				//conn.connect(user.getUsername(), user.getPassword());
			}catch (Exception e){
				e.printStackTrace();
				return "redirect:/login";
			}
		}
		return "redirect:/dashboard";
	}


}
