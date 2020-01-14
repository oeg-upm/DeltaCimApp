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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.repository.model.Route;
import cim.repository.service.BridgingService;

@Controller
public class BridgingController extends AbstractController{
	
	@Autowired
	@Singleton
	public BridgingService bridgingService;
	
	// Provide GUI
	
	@RequestMapping(value="/bridging", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getBridgingService(Model model) {
		model.addAttribute("routes", bridgingService.getAllRoutes());
		Route route = new Route();
		route.setRegexPath("");
		route.setEndpoint("");
		model.addAttribute("route", route);
		if(!model.containsAttribute("errorEmptyRegex"))
			model.addAttribute("errorEmptyRegex", false);
		if(!model.containsAttribute("errorEmptyEndpoint"))
			model.addAttribute("errorEmptyEndpoint", false);
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
		model.addAttribute("errorEmptyRegex", false);
		model.addAttribute("errorEmptyEndpoint", false);
		if(isLogged()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			if(bindingResult.hasErrors()) {
				if(route.getRegexPath().isEmpty())
					model.addAttribute("errorEmptyRegex", true);
				if(route.getEndpoint().isEmpty())
					model.addAttribute("errorEmptyEndpoint", true);
			}else {
				bridgingService.routeRepository.save(route);
			}
		}else {
			// Instead of the error code redirects to the login
			return "redirect:/login";
		}
		System.out.println(model.asMap().get("errorEmptyRegex"));
		return "redirect:/bridging";
	}
	
	@RequestMapping(value="/api/route/{routeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public String deleteRoute(@PathVariable String routeId, HttpServletResponse response) {
		prepareResponse(response);
		if(isLogged() && !routeId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			bridgingService.remove(routeId);
		}else {
			// Instead of the error code redirects to the login
			return "redirect:/login";
		}
		return "redirect:/bridging";
	}

	
}
