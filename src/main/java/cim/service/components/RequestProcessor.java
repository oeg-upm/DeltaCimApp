package cim.service.components;

import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cim.ConfigTokens;
import cim.factory.PayloadsFactory;
import cim.factory.RequestsFactory;
import cim.model.BridgingRule;
import cim.model.P2PMessage;
import cim.service.BridgingService;
import cim.service.KGService;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;
import helio.framework.objects.Tuple;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class RequestProcessor {

	@Autowired
	public BridgingService bridgingService;
	@Autowired
	public KGService kgService;

	
	private Logger log = Logger.getLogger(RequestProcessor.class.getName());
		
	public Tuple<String,Integer> fetchData(P2PMessage message) {
		Tuple<String,Integer> response =  new Tuple<>("{\"error\":\"internal error\"}", 500);
		BridgingRule matchedRoute = matchBridgingRoute(message);
		try{
			if(matchedRoute!=null) {
				String realLocalEndpoint = RequestsFactory.buildRealLocalEndpoint(message, matchedRoute);
				System.out.println("Local endpoint requested: "+realLocalEndpoint);
				response = sendRequest(message, realLocalEndpoint, matchedRoute);
			}else {
				//TODO : add payload for not found route
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
		sparqlRoute.setXmppPattern("^/sparql.*");
		sparqlRoute.setEndpoint("http://localhost:"+ConfigTokens.SERVER_PORT+"/api/sparql");
		return sparqlRoute;
	}
	
	private BridgingRule matchBridgingRoute(P2PMessage message) {
		BridgingRule matchedRoute = null;
		List<BridgingRule> routes = bridgingService.getAllRoutes();
		routes.add(createSPARQLRoute());

		int maxSize = routes.size();
		for(int index=0; index < maxSize; index++) {
			BridgingRule matchedRouteAux =  routes.get(index);
			if(match(matchedRouteAux.getXmppPattern(), message.getRequest())) {
				matchedRoute = matchedRouteAux;
				break;
			}
		}
		
		return matchedRoute;
	}
	

	
	private boolean match(String pattern,String value) {
		Boolean match = false; 
		Pattern regexPattern = Pattern.compile(pattern);
	    Matcher matcher = regexPattern.matcher(value);
	    if (matcher.find()) 
	    		match = true;
		return match;
	}
	
	
	private Tuple<String,Integer> sendRequest(P2PMessage message, String endpoint, BridgingRule rule) throws UnirestException {
		Tuple<String, Integer> requestResponse = null;
		String methodNormalized = message.getMethod().trim().toLowerCase();
		String responseMessage = null;
		String headers = message.getHeaders();
		Integer code = 200;
			if(methodNormalized.equals("get") && !endpoint.contains("/sparql")) {
				// DONE: if request is a non-sparql GET Helio solves it
				RDF responseMessageRDF = KGService.virtualiseRDF(endpoint, rule.getReadingMapping(), headers);
				if(responseMessageRDF!=null) {
					responseMessage = responseMessageRDF.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION);
					code = kgService.validateRDF(responseMessage, endpoint);
					if(code!=200)
						responseMessage = ConfigTokens.ERROR_JSON_MESSAGES_4;
				}else {
					responseMessage = PayloadsFactory.getRequestErrorPayload().getFirstElement();
					code = PayloadsFactory.getRequestErrorPayload().getSecondElement();
				}
				 requestResponse = new Tuple<>(responseMessage, code);
			}else if(endpoint.contains("/sparql")) {
				String query = null;
				if(methodNormalized.equals("post"))
					query = message.getMessage();
				if(methodNormalized.equals("get"))
					query = retrieveSPARQLQuery(endpoint);
				if(query!=null) {
					 Tuple<String,Integer> tuple = kgService.solveQuery(query, SparqlResultsFormat.JSON, message, headers);
					 responseMessage = tuple.getFirstElement();
					 code = tuple.getSecondElement();
					 if(code!=200)
							responseMessage = ConfigTokens.ERROR_JSON_MESSAGES_4;
				}else {
					responseMessage = PayloadsFactory.getRequestErrorPayload().getFirstElement();
					code = PayloadsFactory.getRequestErrorPayload().getSecondElement();
				}
				requestResponse = new Tuple<>(responseMessage, code);
			}else if(methodNormalized.equals("post") && !endpoint.contains("/sparql")) {
				requestResponse = solvePostRequest(message, endpoint, rule, headers);
				if(requestResponse.getSecondElement()!=200)
					requestResponse.setFirstElement(ConfigTokens.ERROR_JSON_MESSAGES_5);
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
			
		return requestResponse;
	}
	

	
	
	 
			
	
	
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
	

	private Tuple<String,Integer> solvePostRequest(P2PMessage message, String endpoint, BridgingRule rule, String headers) {
		Tuple<String,Integer> tuple = new Tuple<>(); 
		 try {
			 // If this route has no mapping associated perform the POST with current body
			 String requestBody = message.getMessage();
			 Map<String,String> headersMap = retrieveHeaders(headers);
			 Integer code = kgService.validateRDF(requestBody, endpoint);
			 tuple.setSecondElement(code);
			 if(code==200 && rule.getWrittingMapping()!=null && !rule.getWrittingMapping().isEmpty()) {
				 //requestBody = devirtualizeData(requestBody, rule.getWrittingMapping());
			 }
			 if(code!=200) {
				 tuple.setFirstElement("Provided data has validation errors");
				 tuple.setSecondElement(418);
			 }else if(requestBody==null) {
				 tuple.setFirstElement(ConfigTokens.ERROR_JSON_MESSAGES_5);
				 tuple.setSecondElement(418);
			 }else{
				 String responseMessage = null;
				 if(code==200) {
					 HttpResponse<String> response = Unirest.post(endpoint).headers(headersMap).body(requestBody).asString();
					 tuple.setSecondElement(response.getStatus());
					 responseMessage = response.getBody();
				 }
				 tuple.setFirstElement(responseMessage);
			 }
		 } catch (UnirestException e) {
				e.printStackTrace();
				System.out.println(endpoint);
		}
		 return tuple;
	}
	
	


	private Map<String,String> retrieveHeaders(String headersStr){
		Map<String,String> headers = new HashMap<>();
		try {
			JSONObject headersJson = new JSONObject(headersStr);
			@SuppressWarnings("unchecked")
			Iterator<String> keys = headersJson.keys();
			while(keys.hasNext()) {
				String key = keys.next();
				if(!key.equals("content-length")){
					String entry = headersJson.getString(key);
					headers.put(key, entry);
				}
			}
				
		}catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Error processing headers for POST request");
		}
		return headers;
	}
}
