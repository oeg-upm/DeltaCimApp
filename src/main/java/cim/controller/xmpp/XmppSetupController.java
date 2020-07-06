package cim.controller.xmpp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.factory.PayloadsFactory;
import cim.model.XmppUser;
import cim.model.enums.ConnectionStatus;
import cim.service.XMPPService;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class XmppSetupController extends AbstractSecureController{

	@Autowired
	public XMPPService xmppService;

	@ApiIgnore
	@RequestMapping(value = "/xmpp", method = RequestMethod.GET, produces = { "text/html", "application/xhtml+xml", "application/xml" })
	public String getDashboard(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if (authenticated(request)) {
			template = "xmpp.html";
		} else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
	}

	// -- API methods
	
	@ApiOperation(value = "Stores the xmpp configuration required to connect to the peer-to-peer network")
	@RequestMapping(value="/api/xmpp/user", method = RequestMethod.GET, produces="application/json")
	@ResponseBody
	public XmppUser updateXmppUser(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		XmppUser xmppUser = new XmppUser();
		if(authenticated(request)) {
			xmppUser = xmppService.getXmppUser();
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return xmppUser;
	}
	

	
	@ApiOperation(value = "Stores the xmpp configuration required to connect to the peer-to-peer network")
	@RequestMapping(value="/api/xmpp/user", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public void updateXmppUser(HttpServletRequest request, HttpServletResponse response, @RequestBody(required=true) @Valid XmppUser xmppUser) {
		prepareResponse(response);
		if(authenticated(request)) {
			xmppService.updateXmppUser(xmppUser);
			response.setStatus(HttpServletResponse.SC_CREATED);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
	
	@ApiOperation(value = "Returns the state of the xmpp connection")
	@RequestMapping(value="/api/xmpp/status", method = RequestMethod.GET)
	@ResponseBody
	public String connectStatus(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);		
		String payload = null;
		if(authenticated(request)) {
			String xmppUser =  null;
			if(xmppService.getXmppUser()!=null) {
				xmppUser = xmppService.getXmppUser().getUsername();
			}
			payload = PayloadsFactory.getXmppConnectionStatusPayload(xmppUser, xmppService.isConnected());
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return payload;
	}
	
	@ApiOperation(value = "Connects to the peer-to-peer network with the established xmpp-user")
	@ApiResponses(value = {
	        @ApiResponse(code = 405, message = "Already connected"),
	        @ApiResponse(code = 409, message = "Error ocurred connecting to xmpp server"),
	 })
	@RequestMapping(value="/api/xmpp/connect", method = RequestMethod.POST)
	@ResponseBody
	public String connect(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);		
		String payload = null;
		if(authenticated(request)) {
			xmppService.disconnect();
			ConnectionStatus status = xmppService.connect();
			payload = PayloadsFactory.getXmppConnectPayload(xmppService.isConnected(), xmppService.getXmppUser().getUsername(), status);
			if(!xmppService.isConnected())
				response.setStatus(HttpServletResponse.SC_CONFLICT);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}	
		return payload;
	}

	@ApiOperation(value = "Disconnects from the peer-to-peer network")
	@ApiResponses(value = {
	        @ApiResponse(code = 409, message = "Disconnection failed"),
	 })
	@RequestMapping(value="/api/xmpp/disconnect", method = RequestMethod.POST)
	@ResponseBody
	public void disconnect(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);		
		if(authenticated(request)) {
			xmppService.disconnect();
			if(xmppService.isConnected()) {
				response.setStatus(HttpServletResponse.SC_CONFLICT);
			}
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

}
