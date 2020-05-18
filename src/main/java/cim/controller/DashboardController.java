package cim.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.model.XmppUser;
import cim.service.UserService;
import cim.service.XMPPService;

@Controller
public class DashboardController extends AbstractController{

	@Autowired
	public XMPPService xmppService;

	@Autowired
	public UserService userService;

	@PostConstruct
	public void initXmpp() {
		xmppService.createDefaultXmppUser();
	}

	@PreDestroy
	public void disconnectPeer() {
		xmppService.disconnect();
	}

	@RequestMapping(value="/dashboard", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getDashboard(Model model, HttpServletResponse response) {
		prepareResponse(response);
		XmppUser xmppUser = xmppService.getXmppUser();
		if(xmppUser != null) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			model.addAttribute("xmppUser", xmppUser);
			model.addAttribute("isConnected", xmppService.isConnected());
			model.addAttribute("connectionError", XMPPService.getConnectionError());
		}
		return "dashboard.html";
	}


	@RequestMapping(value="/api/dashboard", method = RequestMethod.POST)
	public String connect(@ModelAttribute(value="xmppUser") XmppUser xmppUser, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response, Model model) {
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
				xmppService.connect(xmppUser.getUsername(), xmppUser.getPassword(), xmppUser.getXmppDomain(), xmppUser.getHost(), xmppUser.getPort(), xmppUser.getFileCA());
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
		if(xmppService.isConnected()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);
			boolean estaDesconectado = xmppService.disconnect();
			if(estaDesconectado) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
			}
		}
	}

}
