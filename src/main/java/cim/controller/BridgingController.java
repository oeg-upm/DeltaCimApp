package cim.controller;

import java.util.ArrayList;
import java.util.List;
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

import cim.model.Route;
import cim.service.BridgingService;

@Controller
public class BridgingController extends AbstractController{
	
	@Autowired
	public BridgingService bridgingService;
	
	// Provide GUI
	
	@RequestMapping(value="/bridging", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getBridgingService(Model model) {
		if(!isLogged()) {
			return "redirect:/login";
		}
		model.addAttribute("routes", bridgingService.getAllRoutes());
		Route route = new Route();
		route.setRegexPath("");
		route.setEndpoint("");
		route.setFormat("");
		model.addAttribute("route", route);
		return "bridging.html";
	}
	
	// API Routes
		
	@RequestMapping(value="/api/routes", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<Route> getAllRoutes(HttpServletResponse response) {
		prepareResponse(response);
		
		List<Route> routes =  new ArrayList<>();
		if(isLogged()) {
			routes = bridgingService.getAllRoutes();
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} // By default returns the error code
		return routes;
	}
	
		
	@RequestMapping(value="/api/route", method = RequestMethod.POST)
	public String saveRoute(@Valid @ModelAttribute(value="route") Route route, BindingResult bindingResult, HttpServletResponse response, Model model) {
		prepareResponse(response);
		if(isLogged()) {
			if(!bindingResult.hasErrors()) {	
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				bridgingService.update(route);
			}
		}else {
			// Instead of the error code redirects to the login
			return "redirect:/login";
		}
		return "redirect:/bridging";
	}
	
	@RequestMapping(value="/api/route", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteRoute(@RequestBody(required=true) String routeId, HttpServletResponse response, Model model) {
		System.out.println("Enter");
		prepareResponse(response);		
		if(isLogged() && !routeId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			bridgingService.remove(routeId);
		}
	}
	

	
}
