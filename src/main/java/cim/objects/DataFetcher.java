package cim.objects;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.json.JSONObject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.GetRequest;

import cim.ConfigTokens;
import cim.model.P2PMessage;
import cim.model.ValidationReport;
import cim.repository.ValidationReportRepository;
import cim.model.BridgingRule;
import cim.service.BridgingService;
import cim.service.KGService;
import cim.service.ValidationService;
import cim.xmpp.factory.RequestsFactory;
import cim.xmpp.factory.ValidationReportFactory;
import helio.components.engine.EngineImp;
import helio.framework.MappingTranslator;
import helio.framework.mapping.Mapping;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;
import helio.framework.objects.Tuple;
import helio.mappings.translators.AutomaticTranslator;

public class DataFetcher {

	private Logger log = Logger.getLogger(DataFetcher.class.getName());
		
	public Tuple<String,Integer> fetchData(P2PMessage message) {
		Tuple<String,Integer> response =  new Tuple<>("{\"error\":\"internal error\"}", 500);
		BridgingRule matchedRoute = matchBridgingRoute(message);
		try{
			if(matchedRoute!=null) {
				String realLocalEndpoint = RequestsFactory.buildRealLocalEndpoint(message, matchedRoute);
				System.out.println("Local endpoint requested: "+realLocalEndpoint);
				response = sendRequest(message, realLocalEndpoint, matchedRoute);
			}else {
				log.severe("Endpoint requested was not found: "+message.getRequest());
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	

	private BridgingRule createSPARQLRoute() {
		BridgingRule sparqlRoute = new BridgingRule();
		sparqlRoute.setAppendPath(true);
		sparqlRoute.setRegexPath("/sparql.*");
		sparqlRoute.setEndpoint("http://localhost:"+ConfigTokens.LOCAL_PORT+"/api/sparql");
		sparqlRoute.setMappingsFolder("");
		return sparqlRoute;
	}
	
	private BridgingRule matchBridgingRoute(P2PMessage message) {
		BridgingRule matchedRoute = null;
		List<BridgingRule> routes = new ArrayList<>();
		routes.add(createSPARQLRoute());
		routes.addAll(BridgingService.getRoutes());
		int maxSize = routes.size();
		for(int index=0; index < maxSize; index++) {
			matchedRoute =  routes.get(index);
			if(match(matchedRoute.getRegexPath(), message.getRequest()))
				break;
		}
		return matchedRoute;
	}
	

	
	private boolean match(String pattern,String value) {
		Boolean match = false; 
		Pattern reegexPattern = Pattern.compile(pattern);
	    Matcher matcher = reegexPattern.matcher(value);
	    if (matcher.find()) 
	    		match = true;
		return match;
	}
	
	
	private Tuple<String,Integer> sendRequest(P2PMessage message, String endpoint, BridgingRule rule) throws UnirestException {
		String methodNormalized = message.getMethod().trim().toLowerCase();
		String responseMessage = null;
		String headers = message.getHeaders();
		Integer code = 200;
			if(methodNormalized.equals("get") && !endpoint.contains("/sparql")) {
				// DONE: if request is a non-sparql GET Helio solves it
				RDF responseMessageRDF = KGService.virtualiseRDF(endpoint, rule.getReadingMapping(), headers);
				if(responseMessageRDF!=null) {
					responseMessage = responseMessageRDF.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION);
					code = KGService.validateRDF(responseMessage, endpoint);
				}else {
					code = 409;
				}
			}else if(endpoint.contains("/sparql")) {
				String query = null;
				if(methodNormalized.equals("post"))
					query = message.getMessage();
				if(methodNormalized.equals("get"))
					query = retrieveSPARQLQuery(endpoint);
				if(query!=null) {
					 Tuple<String,Integer> tuple = KGService.solveQuery(query, SparqlResultsFormat.JSON, message, headers);
					 responseMessage = tuple.getFirstElement();
					 code = tuple.getSecondElement();
				}else {
					code = 409;
				}
			}else if(methodNormalized.equals("post") && !endpoint.contains("/sparql")) {
				//responseMessage = solvePostRequest(message, endpoint, rule);
				//code = validateRDF(responseMessage, endpoint);
			}/*else if(methodNormalized.equals("put")) {
				//responseMessage = Unirest.put(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("PUT requests not implemented yet");
			}else if(methodNormalized.equals("delete")) {
				//responseMessage = Unirest.delete(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("DELETE requests not implemented yet");
			}else if(methodNormalized.equals("patch")) {
				//responseMessage = Unirest.patch(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("PATCH requests not implemented yet");
			}*/
		
		return new Tuple<String,Integer>(responseMessage, code);
	}
	
	// -- Validation Methods
	
	
	 
			
	
	
	// -- Solving sparql methods
	
	private String retrieveSPARQLQuery(String endpoint) {
		String sparqlQuery = null;
		try {
			URL url = new URL(endpoint);
			String query = url.getQuery();
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        String keyword = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
		        if(keyword.equals("query") || keyword.equals("update")) {
		        		sparqlQuery = URLDecoder.decode(pair.substring(idx + 1), "UTF-8");
		        		break;
		        }
		    }
		}catch(Exception e) {
			log.severe(e.toString());
		}
	    return sparqlQuery;
	}
	

	
	/// --- Other methods
	

	private String solvePostRequest(P2PMessage message, String endpoint, BridgingRule rule) {
		 String responseMessage = null;
		 /*try {
			 // TODO: si es un POST SI necesitamos transformar el body primero
			 responseMessage = Unirest.post(endpoint).body(message.getMessage()).asString().getBody();
			
		 } catch (UnirestException e) {
				e.printStackTrace();
				System.out.println(endpoint);
			}*/
		 return responseMessage;
	}
}
