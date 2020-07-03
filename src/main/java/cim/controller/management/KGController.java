package cim.controller.management;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cim.ConfigTokens;
import cim.controller.AbstractSecureController;
import cim.service.VirtualisationService;
import helio.framework.objects.Tuple;

@Controller
public class KGController extends AbstractSecureController{
	
	@Autowired
	public VirtualisationService virtualisationService;
	
	// Provide GUI
	
	@RequestMapping(value="/kg", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getKgGUI(HttpServletRequest request, HttpServletResponse response) {
		prepareResponseOK(response);
		String template = ConfigTokens.DEFAULT_TEMPLATE;
		if(authenticated(request)) {
			template = "kg.html";
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return template;
	}

	// -- GET method
	
	@RequestMapping(value="/api/sparql", method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
	@ResponseBody
	public String sparqlEndpointGET(HttpServletRequest request, @RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
		prepareResponseOK(response);
		String responseBody = null;
		if(authenticated(request)) {
			query = virtualisationService.cleanQuery(query);
			Tuple<String,Integer> tupleResult = virtualisationService.solveQuery(query, headers);		
			responseBody = tupleResult.getFirstElement();
			response.setStatus(tupleResult.getSecondElement());
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return responseBody;
	}
	
	// -- POST method
	
	@RequestMapping(value="/api/sparql", method = RequestMethod.POST, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11", "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"}) 
	@ResponseBody
	public String sparqlEndpointPOST(HttpServletRequest request, @RequestHeader Map<String, String> headers, @RequestBody(required = true) String query, HttpServletResponse response) {
		prepareResponseOK(response);
		String responseBody = null;
		if(authenticated(request)) {
			Tuple<String,Integer> tupleResult = virtualisationService.solveQuery(query, headers);		
			responseBody = tupleResult.getFirstElement();
			response.setStatus(tupleResult.getSecondElement());
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		}
		return responseBody;
	}
	

	
}
