package cim.controller.management;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.service.ValidationService;
import helio.framework.objects.RDF;
import helio.framework.objects.Tuple;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cim.model.ValidationReport;
import cim.service.components.RequestProcessor;

@Controller
public class ValidationController extends AbstractSecureController {

	@Autowired
	public ValidationService validationService;
	@Autowired
	public RequestProcessor requestProcessor;
	
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
	



	@RequestMapping(value="/api/validation", method = RequestMethod.POST, produces ="application/json")
	@ResponseBody
	public ValidationReport validateCustomRDF(@RequestParam(required=true) String endpoint, HttpServletResponse response, HttpServletRequest request) {
		prepareResponse(response);
		ValidationReport validationReport = null;
		prepareResponseOK(response);		
		if(authenticated(request)) {
			Map<String,String> headers = new HashMap<>();
			Tuple<String, Integer> payload = requestProcessor.solveGetRequest(endpoint, headers);
			if(payload.getSecondElement()==null) {
				validationReport = new ValidationReport();
				validationReport.setReport("The provided local endpoint is not available");
				payload.setSecondElement(400);
			}else if(payload.getSecondElement()==200) {
				RDF rdfData = new RDF();
				rdfData.parseRDF(payload.getFirstElement(), ConfigTokens.DEFAULT_RDF_SERIALISATION);
				validationReport = validationService.validatePaylad(rdfData, endpoint);
				response.setStatus(HttpServletResponse.SC_ACCEPTED);	
			}else if(payload.getSecondElement()==218) {
				validationReport = new ValidationReport();
				validationReport.setReport("Endpoint payload is not interoperable");
			}
			response.setStatus(payload.getSecondElement());
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return validationReport;
	}

	
}
