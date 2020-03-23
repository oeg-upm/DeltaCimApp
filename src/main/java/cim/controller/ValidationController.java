package cim.controller;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.ConfigTokens;
import cim.model.ValidationReport;
import cim.service.ValidationService;

@Controller
public class ValidationController extends AbstractController {

	@Autowired
	public ValidationService validationService;
		
	// Provide GUI
	
	@RequestMapping(value="/validation", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getValidationService(Model model, HttpServletRequest request) {
		if(!isLogged(request)) {
			return "redirect:/login";
		}
		model.addAttribute("reports", validationService.getAllReports());
		return "validation.html";
	}
	
	// API Routes
		
	@RequestMapping(value="/api/validation/reports", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<ValidationReport> getAllReports(HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		List<ValidationReport> reports =  new ArrayList<>();
		if(isLogged(request)) {
			reports = validationService.getAllReports();
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} // By default returns the error code
		return reports;
	}
	
	@RequestMapping(value="/api/validation/report", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteReport(@RequestBody(required=true) String reportId, HttpServletResponse response, Model model, HttpServletRequest request) {
		prepareResponse(response);		
		if(isLogged(request) && !reportId.isEmpty()) {
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			validationService.remove(reportId);
		}
	}
	

	
	@RequestMapping(value="/api/validation/endpoint", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public ValidationReport validateRDFEndpoint(@RequestParam String address, @RequestParam String format, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = new ValidationReport();
		validationReport.setReport(ConfigTokens.VALIDATIONS_SHAPES_SUCCESS_MESSAGE);
		if(isLogged(request)) {
			ValidationReport validationReportAux = validationService.validateRDFEndpoint(address, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			if(validationReportAux!=null) {
				validationReport = validationReportAux;
			}
		} else {
			//return to login
		}
		return validationReport;
	}
	
	@RequestMapping(value="/api/validation/document", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public ValidationReport validateRDF(@RequestParam String format, @RequestBody String data, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = null;
		if(isLogged(request)) {
			validationReport = new ValidationReport();
			validationService.validateRDF(data, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} else {
			//return to login
		}
		return validationReport;
	}
	

	@RequestMapping(value="/api/validation", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public ValidationReport validateCustomRDF(@RequestParam String format, @RequestBody String data, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = null;
		if(isLogged(request)) {
			validationReport = new ValidationReport();
			validationService.validateRDF(data, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} else {
			//return to login
		}
		return validationReport;
	}

	
}
