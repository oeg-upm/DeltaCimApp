package cim.controller.management;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.factory.InteroperabilityModuleFactory;
import cim.model.BridgingRule;
import cim.model.InteroperabilityModule;
import cim.model.enums.Method;
import cim.service.BridgingService;
import helio.framework.objects.Tuple;

@Controller
public class BridgingController extends AbstractSecureController{
	
	@Autowired
	public BridgingService bridgingService;

	// Provide GUI
	
	@RequestMapping(value="/bridging", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getBridgingService(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if(authenticated(request)) {
			template = "bridging.html";
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
		
	}
	
	
	// API Modules
	
	@RequestMapping(value="/api/modules", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<InteroperabilityModule> getModules(HttpServletResponse response, HttpServletRequest request) {
		List<InteroperabilityModule> modules = new ArrayList<>();
		if(authenticated(request)) {
			File folder = new File(ConfigTokens.MODULES_FOLDER);
			File[] listOfFiles = folder.listFiles();
			for(File file:listOfFiles) {
				if(file.getName().endsWith(ConfigTokens.MODULES_FILE_EXTENSION)) {
					InteroperabilityModule intModule = new InteroperabilityModule();
					intModule.setName(file.getName());
					intModule.setFile(file.getPath());
					Tuple<String,String> module = InteroperabilityModuleFactory.readInteroperabilityModule(file.getPath());
					if(module.getFirstElement()!=null)
						intModule.setMethod(Method.GET);
					if(module.getSecondElement()!=null)
						intModule.setMethod(Method.POST);
					modules.add(intModule);
				}
			}
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return modules;
	}
	
	// API Routes
		
	@RequestMapping(value="/api/routes", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<BridgingRule> getAllRoutes(HttpServletResponse response, HttpServletRequest request) {
		prepareResponseOK(response);
		List<BridgingRule> routes =  new ArrayList<>();
		if(authenticated(request)) {
			routes = bridgingService.getAllRoutes();
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return routes;
	}
	
		
	@RequestMapping(value="/api/routes", method = RequestMethod.POST, consumes ="application/json")
	@ResponseBody
	public void saveRoute(HttpServletRequest request, HttpServletResponse response, @RequestBody(required=true) @Valid BridgingRule route) {
		prepareResponseOK(response);
		if(authenticated(request)) {
			if(route.getInteroperabilityModuleFile().endsWith("None") ||route.getInteroperabilityModuleFile().isEmpty()) {
				route.setInteroperabilityModuleFile(null);
			}
			route.setReadingMapping(null);
			route.setWrittingMapping(null);
			route.updateMappingContent();
			bridgingService.update(route);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
		
	
	
	
	@RequestMapping(value="/api/routes", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteRoute(@RequestParam(required=true) long routeId, HttpServletResponse response, Model model, HttpServletRequest request) {
		prepareResponseOK(response);		
		if(authenticated(request)) {
			bridgingService.remove(routeId);
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}
	
	
	

	
}
