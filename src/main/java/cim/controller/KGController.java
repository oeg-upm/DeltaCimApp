package cim.controller;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

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

import cim.service.KGService;
import cim.xmpp.factory.RequestsFactory;
import helio.framework.objects.SparqlResultsFormat;
import helio.framework.objects.Tuple;

@Controller
public class KGController extends AbstractSparqlController{
	
	
	private Logger log = Logger.getLogger(KGController.class.getName());
	

	// Provide GUI
	
	@RequestMapping(value="/kg", method = RequestMethod.GET, produces = {"text/html", "application/xhtml+xml", "application/xml"})
	public String getKgGUI(Model model) {
		return "kg.html";
	}
	
	@RequestMapping(value="/api/sparql", method = RequestMethod.GET, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11",  "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"})
	@ResponseBody
	public String sparqlEndpointGET(@RequestHeader Map<String, String> headers, @RequestParam (required = true) String query, HttpServletResponse response) {
		return solveQuery(query, headers, response);
	}
	
	// -- POST method
	
	@RequestMapping(value="/api/sparql", method = RequestMethod.POST, produces = {"application/sparql-results+xml", "text/rdf+n3", "text/rdf+ttl", "text/rdf+turtle", "text/turtle", "text/n3", "application/turtle", "application/x-turtle", "application/x-nice-turtle", "text/rdf+nt", "text/plain", "text/ntriples", "application/x-trig", "application/rdf+xml", "application/soap+xml", "application/soap+xml;11", "application/vnd.ms-excel", "text/csv", "text/tab-separated-values", "application/javascript", "application/json", "application/sparql-results+json", "application/odata+json", "application/microdata+json", "text/cxml", "text/cxml+qrcode", "application/atom+xml"}) 
	@ResponseBody
	public String sparqlEndpointPOST(@RequestHeader Map<String, String> headers, @RequestBody(required = true) String query, HttpServletResponse response) {
		return solveQuery(query, headers, response);
	}
	

	/**
	 * This method solves a SPARQL query using the semantic engine. By default answer is in JSON format
	 * @param query A SPARQL query to solve
	 * @param headers A set of headers that should contain the format to display the query answer (it can be null or not contain 'Accept' header).
	 * @param response A {@link HttpServletResponse} containing the possible responses, i.e., OK (200), syntax error (400), error processing query or fetching data or missing mappings (500)
	 * @return The query answer in the specified format
	 */
	private String solveQuery(String query, Map<String, String> headersMap, HttpServletResponse response) {
		String result = "";
		prepareResponse(response);
		try {
			query = cleanQuery(query);
			SparqlResultsFormat specifiedFormat = extractResponseAnswerFormat(headersMap);
			// Solve query
			String headers = RequestsFactory.fromHeadersMaptoString(headersMap);
			Tuple<String,Integer> resultTuple = KGService.solveQuery(query, specifiedFormat, null, headers);
			// Check results
			response.setStatus(resultTuple.getSecondElement());
			String queryResults = resultTuple.getFirstElement();
			if(queryResults == null) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				log.info("Query has syntax errors");
			}else {
				result = queryResults;
				log.info("Query solved");
			}
		} catch (Exception e) {
			log.severe(e.getMessage());
		}
		return result;
	}

	private String cleanQuery(String query) {
		String cleanedQuery = query;
		try {
			// When query comes from the get it has the query=...
			if (cleanedQuery.startsWith("query="))
				cleanedQuery = cleanedQuery.substring(6);
			if (cleanedQuery.startsWith("update="))
				cleanedQuery = cleanedQuery.substring(7);
			cleanedQuery = java.net.URLDecoder.decode(cleanedQuery, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.severe(e.toString());
		}
		return cleanedQuery;
	}

	
}
