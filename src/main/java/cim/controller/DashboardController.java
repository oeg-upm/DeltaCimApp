package cim.controller;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
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
import cim.model.XmppUser;
import cim.repository.XmppRepository;
import cim.service.ConnectionService;
import cim.service.UserService;
import cim.service.XMPPService;

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
		return "dashboard.html";
	}


	@RequestMapping(value="/api/dashboard", method = RequestMethod.POST)
	public String connect(@Valid @ModelAttribute(value="xmppUser") XmppUser xmppUser, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Model model) {
		prepareResponse(response);
		//Connect only if user has authority, admin always have role user
		if(isUser(request)) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			//if xmppUser is valid, then use its values to connect
			try {
				if(!bindingResult.hasErrors()) {
					//if the user is an admin, update the values of the connection
					xmppService.updateXmppUser(xmppUser);
				}else{
					//Otherwise, use default XmppUser
					xmppUser = xmppService.getXmppUser();
				}
				loginService.connect(xmppUser.getUsername(), xmppUser.getPassword(), xmppUser.getXmppDomain(), xmppUser.getHost(), xmppUser.getPort(), xmppUser.getFileCA());
			}catch (Exception e){
				e.printStackTrace();
			}
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

}
