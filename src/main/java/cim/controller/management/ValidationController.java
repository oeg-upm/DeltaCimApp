package cim.controller.management;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import cim.model.ValidationReport;
import cim.service.ValidationService;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@Controller
public class ValidationController extends AbstractSecureController {

	@Autowired
	public ValidationService validationService;
		
	// Provide GUI
	@ApiIgnore
	@RequestMapping(value="/validation", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getValidationService(HttpServletRequest request,  HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if(authenticated(request)) {
			template = "validation.html";
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
	}
	
	// API Routes
	@ApiOperation(value = "Returns the list of valdiation reports")
	@RequestMapping(value="/api/validation/report", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public List<ValidationReport> getAllReports(HttpServletResponse response, HttpServletRequest request) {
		prepareResponseOK(response);
		List<ValidationReport> reports =  new ArrayList<>();
		if(authenticated(request)) {
			reports = validationService.getAllReports();
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return reports;
	}
	
	@ApiOperation(value = "Deletes an existing valdiation report")
	@RequestMapping(value="/api/validation/report", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteReport(@RequestParam(required=true) @NotEmpty String reportId, HttpServletResponse response, HttpServletRequest request) {
		prepareResponseOK(response);		
		if(authenticated(request)) {
			validationService.remove(reportId);	
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
	}
	
	// From here below not checked
	
	@RequestMapping(value="/api/validation/endpoint", method = RequestMethod.GET, produces ="application/json")
	@ResponseBody
	public ValidationReport validateRDFEndpoint(@RequestParam String address, @RequestParam String format, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = new ValidationReport();
		validationReport.setReport(ConfigTokens.VALIDATIONS_SHAPES_SUCCESS_MESSAGE);
		/*if(isLogged(request)) {
			ValidationReport validationReportAux = validationService.validateRDFEndpoint(address, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			if(validationReportAux!=null) {
				validationReport = validationReportAux;
			}
		} else {
			//return to login
		}*/
		return validationReport;
	}
	
	@RequestMapping(value="/api/validation/document", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public ValidationReport validateRDF(@RequestParam String format, @RequestBody String data, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = null;
		/*if(isLogged(request)) {
			validationReport = new ValidationReport();
			validationService.validateRDF(data, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} else {
			//return to login
		}*/
		return validationReport;
	}
	

	@RequestMapping(value="/api/validation", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public ValidationReport validateCustomRDF(@RequestParam String format, @RequestBody String data, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = null;
		/*if(isLogged(request)) {
			validationReport = new ValidationReport();
			validationService.validateRDF(data, format);
			response.setStatus(HttpServletResponse.SC_ACCEPTED);	
		} else {
			//return to login
		}*/
		return validationReport;
	}

	
}
