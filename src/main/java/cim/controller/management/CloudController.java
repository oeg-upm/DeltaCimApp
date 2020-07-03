package cim.controller.management;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import cim.controller.AbstractSecureController;
import cim.factory.StringFactory;
import cim.service.CloudService;
import helio.framework.objects.Tuple;

@Controller
public class CloudController extends AbstractSecureController {

	@Autowired
	public CloudService cloudService;
	
	// --
	
	// Provide GUI
	
		@RequestMapping(value="/cloud", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
		public String getClopudAccessGUI(Model model) {
			return "cloud.html";
		}
	
		@RequestMapping(value="/api/cloud/sparql", method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
		@ResponseBody
		public String federateQueryGET(HttpServletRequest request, @RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
			prepareResponseOK(response);
			String responseBody = null;
			if(authenticated(request)) {
				String token = StringFactory.concatenateStrings(BEARER_TOKEN, retrieveTokenFromCookie(request));
				if(headers.containsKey(AUTHORTISATION_TOKEN)) {
					token = headers.get(AUTHORTISATION_TOKEN);
				}
				 Tuple<String,Integer> responseTuple = cloudService.federateQuery(query, headers,token);
				 responseBody = responseTuple.getFirstElement();
				 response.setStatus(responseTuple.getSecondElement());
			}else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			return responseBody;
		}
		
		// -- POST method
		
		@RequestMapping(value="/api/cloud/sparql", method = RequestMethod.POST, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11", "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"}) 
		@ResponseBody
		public String federateQueryPOST(HttpServletRequest request, @RequestHeader Map<String, String> headers, @RequestBody(required = true) String query, HttpServletResponse response) {
			prepareResponseOK(response);
			String responseBody = null;
			if(authenticated(request)) {
				String token = StringFactory.concatenateStrings(BEARER_TOKEN, retrieveTokenFromCookie(request));
				if(headers.containsKey(AUTHORTISATION_TOKEN)) {
					token = headers.get(AUTHORTISATION_TOKEN);
				}
				 Tuple<String,Integer> responseTuple = cloudService.federateQuery(query, headers,token);
				 responseBody = responseTuple.getFirstElement();
				 response.setStatus(responseTuple.getSecondElement());
			}else {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			return responseBody;
			
		}
		

		
	
	
}
