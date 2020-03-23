package cim.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.http.HttpServletRequest;
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

import cim.ConfigTokens;
import cim.model.BridgingRule;
import cim.service.BridgingService;
import helio.framework.objects.Tuple;

@Controller
public class BridgingController extends AbstractController{
	
	@Autowired
	public BridgingService bridgingService;
	private Logger log = Logger.getLogger(BridgingController.class.getName());

	// Provide GUI
	
	@RequestMapping(value="/bridging", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getBridgingService(Model model, HttpServletRequest request) {
		if(!isLogged(request)) {
			return "redirect:/login";
		}
		model.addAttribute("routes", bridgingService.getAllRoutes());
		BridgingRule route = new BridgingRule();
		route.setRegexPath("");
		route.setEndpoint("");
		route.setMappingsFolder("");
		route.setReadingMapping("");
		route.setWrittingMapping("");
		model.addAttribute("route", route);
		List<String> modules = readAvailableModules();
		model.addAttribute("modules",modules);
		return "bridging.html";
	}
	
	private List<String> readAvailableModules(){
		List<String> modules = new ArrayList<>();
		File folder = new File(ConfigTokens.MODULES_FOLDER);
		File[] listOfFiles = folder.listFiles();
		for(File file:listOfFiles) {
			if(file.getName().endsWith(ConfigTokens.MODULES_FILE_EXTENSION)) {
				modules.add(file.getName());
			}
		}
		return modules;
	}
	
	// API Routes
		
	@RequestMapping(value="/api/routes", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<BridgingRule> getAllRoutes(HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		
		List<BridgingRule> routes =  new ArrayList<>();
		if(isLogged(request)) {
			routes = bridgingService.getAllRoutes();
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} // By default returns the error code
		return routes;
	}
	
		
	@RequestMapping(value="/api/route", method = RequestMethod.POST)
	public String saveRoute(@Valid @ModelAttribute(value="route") BridgingRule route, BindingResult bindingResult, HttpServletResponse response, Model model, HttpServletRequest request) {
		prepareResponse(response);

		if(isLogged(request)) {
			if(!bindingResult.hasErrors()) {	
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
				Tuple<String,String> mappings = readInteroperabilityModule(route);
				route.setReadingMapping(mappings.getFirstElement());
				route.setWrittingMapping(mappings.getSecondElement());
				bridgingService.remove(route.getRegexPath());
				bridgingService.update(route);
			}
		}else {
			// Instead of the error code redirects to the login
			return "redirect:/login";
		}
		return "redirect:/bridging";
	}
		
	private Tuple<String, String> readInteroperabilityModule(BridgingRule route) {
		Tuple<String, String> mappingsContent = new Tuple<>();
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(ConfigTokens.MODULES_BASE_DIR + route.getMappingsFolder());
			for (Enumeration<? extends ZipEntry> e = zipFile.entries(); e.hasMoreElements();) {
				ZipEntry zipEntry = (ZipEntry) e.nextElement();
				String fileName = ConfigTokens.MODULES_BASE_DIR + zipEntry.getName();
				if (isReadingFile(fileName)) {
					String rawMappping = readZippedFile(zipFile, zipEntry);
					mappingsContent.setFirstElement(rawMappping);
				} else if (isWrittingFile(fileName)) {
					mappingsContent.setSecondElement(readZippedFile(zipFile, zipEntry));
				}
			}
		} catch (IOException e) {
			log.severe(e.toString());
		} finally {
			if (zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException e) {
					log.severe(e.toString());
				}
			}
		}
		return mappingsContent;
	}
	
	private boolean isReadingFile(String fileDir) {
		return fileDir.contains(ConfigTokens.MODULES_BASE_DIR_READING) && !fileDir.contains(".DS_Store") && !fileDir.endsWith(ConfigTokens.MODULES_BASE_DIR_READING);
	}
	
	private boolean isWrittingFile(String fileDir) {
		return fileDir.contains(ConfigTokens.MODULES_BASE_DIR_WRITTING) && !fileDir.contains(".DS_Store") && !fileDir.endsWith(ConfigTokens.MODULES_BASE_DIR_WRITTING);
	}
	
	private String readZippedFile(ZipFile zipFile, ZipEntry zipEntry) throws IOException {
		StringBuilder fileContent = new StringBuilder();
         BufferedReader zipReader = new BufferedReader(new InputStreamReader(zipFile.getInputStream(zipEntry)));
         while (zipReader.ready()) {
        	 	fileContent.append(zipReader.readLine()).append("\n");
         }
         zipReader.close();
         String data = null;
         if(!fileContent.toString().isEmpty())
        	 	data = fileContent.toString();
         return data;
	}
	
	
	
	@RequestMapping(value="/api/route", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteRoute(@RequestBody(required=true) String routeId, HttpServletResponse response, Model model, HttpServletRequest request) {
		prepareResponse(response);		
		if(isLogged(request) && !routeId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			bridgingService.remove(routeId);
		}
	}
	
	
	

	
}
