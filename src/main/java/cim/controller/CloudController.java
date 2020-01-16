package cim.controller;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;
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

import cim.service.CloudService;
import helio.framework.objects.SparqlResultsFormat;

@Controller
public class CloudController  extends AbstractSparqlController {

	@Autowired
	public CloudService cloudService;
	private Logger log = Logger.getLogger(CloudController.class.getName());
	
	// --
	
	// Provide GUI
	
		@RequestMapping(value="/cloud", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
		public String getClopudAccessGUI(Model model) {
			return "cloud.html";
		}
		
		@RequestMapping(value="/api/cloud/sparql", method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
		@ResponseBody
		public String federateQueryGET(@RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
			return solveQuery(query, headers, response);
		}
		
		// -- POST method
		
		@RequestMapping(value="/api/cloud/sparql", method = RequestMethod.POST, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11", "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"}) 
		@ResponseBody
		public String federateQueryPOST(@RequestHeader Map<String, String> headers, @RequestBody(required = true) String query, HttpServletResponse response) {
			return solveQuery(query, headers, response);
		}
		

		/**
		 * This method solves a SPARQL query using the semantic engine. By default answer is in JSON format
		 * @param query A SPARQL query to solve
		 * @param headers A set of headers that should contain the format to display the query answer (it can be null or not contain 'Accept' header).
		 * @param response A {@link HttpServletResponse} containing the possible responses, i.e., OK (200), syntax error (400), error processing query or fetching data or missing mappings (500)
		 * @return The query answer in the specified format
		 */
		private String solveQuery(String query, Map<String, String> headers, HttpServletResponse response) {
			String result = "";
			prepareResponse(response);
			try {
				if(query.startsWith("query="))
					query = query.substring(6);
				if(query.startsWith("update="))
					query = query.substring(7);
				query = java.net.URLDecoder.decode(query, StandardCharsets.UTF_8.toString());
				SparqlResultsFormat specifiedFormat = extractResponseAnswerFormat(headers);
				result = cloudService.federateQuery(query, specifiedFormat);
				if(result == null) {
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					log.info("Query has syntax errors");
				}else {
					response.setStatus(HttpServletResponse.SC_ACCEPTED);
					log.info("Query solved");
				}
			} catch (Exception e) {
				log.severe(e.getMessage());
			}
			return result;
		}
	
	
	
}
