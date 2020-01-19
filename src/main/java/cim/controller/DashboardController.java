package cim.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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

import cim.model.Acl;
import cim.model.Authority;
import cim.model.User;
import cim.model.XmppUser;
import cim.repository.XmppRepository;
import cim.service.ConnectionService;
import cim.service.UserService;
import cim.service.XMPPService;
import cim.xmpp.factory.AuthorityFactory;

@Controller
public class DashboardController extends AbstractController{

	@Autowired
	public ConnectionService loginService;

	@Autowired
	public XMPPService xmppService;

	@Autowired
	public XmppRepository xmppRepository;

	@Autowired
	public UserService userService;

	@PostConstruct
	public void initXmpp() {
		xmppService.createDefaultXmppUser();
	}

	@PreDestroy
	public void disconnectPeer() {
		loginService.disconnect();
	}


	@RequestMapping(value="/dashboard", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getDashboard(Model model, HttpServletResponse response) {
		prepareResponse(response);
		XmppUser xmppUser = xmppService.getXmppUser();
		if(xmppUser != null) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			model.addAttribute("xmppUser", xmppUser);
			model.addAttribute("isConnected", loginService.isConnected());
		}
		model.addAttribute("users", userService.getAllUsers());
		User user = new User();
		user.setUsername("");
		user.setPassword("");
		user.setAuthority(null);
		model.addAttribute("user", user);
		return "dashboard.html";
	}


	@RequestMapping(value="/api/dashboard", method = RequestMethod.POST)
	public String connect(@Valid @ModelAttribute(value="xmppUser") XmppUser xmppUser, BindingResult bindingResult, HttpServletResponse response, Model model) {
		prepareResponse(response);
		if(!bindingResult.hasErrors()) {	
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			try {
				loginService.connect(xmppUser.getUsername(), xmppUser.getPassword(), xmppUser.getXmppDomain(), xmppUser.getHost(), xmppUser.getPort(), xmppUser.getFileCA());
			}catch (Exception e){
				e.printStackTrace();
				return "redirect:/dashboard";
			}
			xmppService.updateXmppUser(xmppUser);
		}
		return "redirect:/dashboard";
	}


	@RequestMapping(value="/api/disconnect", method = RequestMethod.GET)
	@ResponseBody
	public void disconnect(HttpServletResponse response, Model model) {
		prepareResponse(response);		
		if(loginService.isConnected()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			boolean estaDesconectado = loginService.disconnect();
			if(estaDesconectado) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
			}
		}
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
		return "redirect:/dashboard";
	}

	@RequestMapping(value="/api/cimuser", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteCimUser(@RequestBody(required=true) String userId, HttpServletResponse response, Model model) {
		System.out.println("DELETEANDO");
		prepareResponse(response);		
		if(!userId.isEmpty() ) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			userService.remove(userId);
		}

	}


}
