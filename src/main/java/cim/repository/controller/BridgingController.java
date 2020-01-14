package cim.repository.controller;

import javax.inject.Singleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
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
	public String getBridgingService() {
		return "bridging.html";
	}
	
	// API Routes
	
	@RequestMapping(value="/api/route", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public String getRoute() {
		if(isLogged()) {
			Route route = new Route();
			route.setAppendPath(false);
			route.setRegexPath(".*");
			route.setEndpoint("http://localhost:8080/hello");
			bridgingService.routeRepository.save(route);
		}
		return "{\"tmp\":\"12\"}";
	}
	
	@RequestMapping(value="/api/route", method = RequestMethod.DELETE, produces ="application/json")
	@ResponseBody
	public void deleteRoute() {
		if(isLogged()) {
			
		}
	}
	
	@RequestMapping(value="/api/route", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public void saveRoute( Route route, BindingResult bindingResult) {
		System.out.println(">>>>>"+route);
		if(isLogged()) {
			//bridgingService.routeRepository.save(route);
		}
	}
	
	@RequestMapping(value="/api/routes", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public void getAllRoutes() {
		if(isLogged()) {
			
		}
	}
	
	@RequestMapping(value="/api/routes", method = RequestMethod.DELETE, produces ="application/json")
	@ResponseBody
	public void deleteAllRoutes() {
		if(isLogged()) {
			
		}
	}
	
	@RequestMapping(value="/api/routes", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public void saveAllRoutes() {
		if(isLogged()) {
			
		}
	}
	

	
}
