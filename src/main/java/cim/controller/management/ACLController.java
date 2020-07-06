package cim.controller.management;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.model.Acl;
import cim.service.ACLService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class ACLController extends AbstractSecureController{

	@Autowired
	public ACLService aclService;

	// Provide GUI
	@ApiIgnore
	@RequestMapping(value="/acl", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getAclService(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if(authenticated(request)) {
			template = "acl.html";
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
	}

	// API ACL
	@ApiOperation(value = "Return the list of P2P users allowed to interact with this component")
	@RequestMapping(value="/api/acl", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<Acl> getAllAcl(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		List<Acl> acls = new ArrayList<>();
		if(authenticated(request)) {
			acls = aclService.getXmppAcls();
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return acls;
	}

	@ApiOperation(value = "Stores a new P2P user that will be enabled to interact with this component")
	@RequestMapping(value="/api/acl", method = RequestMethod.POST, consumes="application/json")
	public void saveAcl(HttpServletRequest request, HttpServletResponse response, @RequestBody(required = true) @Valid Acl xmppNewUser) {
		prepareResponse(response);
		if(authenticated(request)) {	
				response.setStatus(HttpServletResponse.SC_CREATED);
				aclService.update(xmppNewUser);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	@ApiOperation(value = "Deletes an existing P2P user that was authorized to interact with this component")
	@RequestMapping(value="/api/acl", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAcl(HttpServletRequest request, @RequestParam(required=true) @NotEmpty String xmppUsername, HttpServletResponse response) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			aclService.remove(xmppUsername);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
