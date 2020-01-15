package cim.repository.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
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

import cim.repository.model.Acl;
import cim.repository.service.ACLService;

@Controller
public class ACLController extends AbstractController{

	@Autowired
	@Singleton
	public ACLService aclService;

	// Provide GUI

	@RequestMapping(value="/acl", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getAclService(Model model) {
		if(!isLogged()) {
			return "redirect:/login";
		}
		model.addAttribute("users", aclService.getAllACL());
		Acl acl = new Acl();
		acl.setUsername("");
		acl.setReadable(false);
		model.addAttribute("user", acl);
		return "acl.html";
	}

	// API ACL

	@RequestMapping(value="/api/acls", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<Acl> getAllAcl(HttpServletResponse response) {
		prepareResponse(response);
		List<Acl> acls =  new ArrayList<>();
		if(isLogged()) {
			acls = aclService.getAllACL();
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} // By default returns the error code
		return acls;
	}


	@RequestMapping(value="/api/acl", method = RequestMethod.POST)
	public String saveAcl(@Valid @ModelAttribute(value="user") Acl acl, BindingResult bindingResult, HttpServletResponse response, Model model) {
		prepareResponse(response);
		if(isLogged()) {
			if(!bindingResult.hasErrors()) {	
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				aclService.aclRepository.save(acl);
			}
		}else {
			// Instead of the error code redirects to the login
			return "redirect:/login";
		}
		return "redirect:/acl";
	}

	@RequestMapping(value="/api/acl", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAcl(@RequestBody(required=true) String aclId, HttpServletResponse response, Model model) {
		prepareResponse(response);		
		if(isLogged() && !aclId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			aclService.remove(aclId);
		}
	}

}
