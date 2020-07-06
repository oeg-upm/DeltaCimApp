package cim.controller.management;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.model.User;
import cim.security.JwtTokenUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class UsersController  extends AbstractSecureController{
	
	
	// -- Users endpoints
	
	@ApiIgnore
	@RequestMapping(value="/users", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getDashboard(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if(authenticated(request)) {
			template = "users.html";
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
	}

	@ApiOperation(value = "Return the list of users allowed to send data with this component and its P2P related users")
	@RequestMapping(value="/api/user", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<User> getUsers(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		List<User> users = new ArrayList<>();
		if(authenticated(request)) {
			users = userService.getAllUsers();
			Collections.sort(users);	
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return users;
	}
	
	@ApiOperation(value = "Stores a new user that will be allowed to send data with this component and its P2P related users")
	@RequestMapping(value="/api/user", method = RequestMethod.POST, consumes="application/json")
	@ResponseBody
	public void updateUser( @RequestBody(required = true) @Valid User user, HttpServletRequest request, HttpServletResponse response) {
		prepareResponse(response);
		if(authenticated(request)) {
			userService.createUser(user);
			response.setStatus(HttpServletResponse.SC_CREATED);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	@ApiOperation(value = "Deletes an existing user registered in this component")
	@RequestMapping(value="/api/user", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteUser(@RequestParam(required=true) @NotEmpty String userId,  HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			userService.remove(userId);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}

	}
	
	
	@ApiOperation(value = "Creates a token for an existing user registered in this component")
	@RequestMapping(value="/api/user/token",  method = RequestMethod.GET)
	@ResponseBody
	public String tokenValidity(@RequestParam(required=true) @NotEmpty String userId, @RequestParam(required=true) @NotEmpty String token, HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String expirationDate = null;
		if(authenticated(request)) {
			User user = userService.getUser(userId);
			JwtTokenUtil jwtUtil = new JwtTokenUtil();
			if(user!=null && user.getTokens().containsKey(token)) {
				try {
					expirationDate = jwtUtil.getTokenExpirationDate(token).toString();
				}catch(Exception e) {
					expirationDate = "Expired";
				}
			}
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return expirationDate;
	}

	
	@ApiOperation(value = "Creates a token for an existing user registered in this component")
	@RequestMapping(value="/api/user/token",  method = RequestMethod.POST)
	@ResponseBody
	public void createAPIToken(@RequestParam(required=true) @NotEmpty String userId, @RequestParam(required=true) @NotEmpty String minutesValidity, HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			User user = userService.getUser(userId);
			JwtTokenUtil jwtUtil = new JwtTokenUtil();
			if(user!=null) {
				String jwtToken = jwtUtil.generateToken(userId, Integer.valueOf(minutesValidity));
				user.getTokens().put(jwtToken, jwtUtil.getTokenExpirationDate(jwtToken).toString());
				userService.updateUser(user);
			}
			
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}

	}
		
	@ApiOperation(value = "Deletes an existing user registered in this component")
	@RequestMapping(value="/api/user/token",  method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteAPIToken(@RequestParam(required=true) @NotEmpty String userId, @RequestParam(required=true) @NotEmpty String token, HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			User user = userService.getUser(userId);
			if(user!=null) {
				user.getTokens().remove(token);
				userService.updateUser(user);
			}
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
	

}
