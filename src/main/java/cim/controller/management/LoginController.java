package cim.controller.management;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import cim.controller.AbstractSecureController;
import cim.model.User;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;



@Controller
public class LoginController extends AbstractSecureController{

	
	// Provide GUI
	@ApiIgnore
	@RequestMapping(value= {"/","/login"}, method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getLoginService(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = "login.html";
		if(authenticated(request)){
			template = "redirect:/xmpp";
		}
		return template;
	}

	@ApiOperation(value = "Logs a user with the username/password and injects a cookie with the access token")
	@RequestMapping(value="/api/login", method = RequestMethod.POST, consumes="application/json")
	public @ResponseBody void logUser(HttpServletRequest request, HttpServletResponse response, @RequestBody(required=true) @Valid User user) {
		prepareResponseOK(response);
		if(!authenticated(request)) {
			if(userService.checkLogin(user)) {
				Cookie cookie = jwtTokenUtil.createTokenCookie(user.getUsername());
				response.addCookie(cookie);
			}else{
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
		}
	}
	
	@ApiOperation(value = "Logs out a user")
	@RequestMapping(value="/api/logout", method = RequestMethod.POST)
	public @ResponseBody void logOutUser(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			Cookie cookie = jwtTokenUtil.createInvalidTokenCookie();
			response.addCookie(cookie);
		}else{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}


}
