package cim.service.components;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cim.ConfigTokens;
import cim.factory.PayloadsFactory;
import cim.factory.RequestsFactory;
import cim.factory.StringFactory;
import cim.model.BridgingRule;
import cim.model.P2PMessage;
import cim.model.ValidationReport;
import cim.model.enums.Method;
import cim.service.BridgingService;
import cim.service.ValidationService;
import cim.service.VirtualisationService;
import helio.framework.objects.RDF;
import helio.framework.objects.Tuple;

@Component
public class RequestProcessor {

	@Autowired
	public BridgingService bridgingService;
	@Autowired
	public ValidationService validationService;

	@Autowired
	public VirtualisationService virtualisationService;
	private static final String SPARQL_REGEX = "/sparql.*";
	
	private Logger log = Logger.getLogger(RequestProcessor.class.getName());
		
	public Tuple<String,Integer> fetchData(P2PMessage message) {
		Tuple<String,Integer> response =  PayloadsFactory.getErrorPayloadRemoteRouteDoesNotExists();
		BridgingRule matchedRoute = matchBridgingRoute(message, message.getMethod());
		try{
			if(matchedRoute!=null) {
				String realLocalEndpoint = RequestsFactory.buildRealLocalEndpoint(message, matchedRoute);
				String logMessage = StringFactory.concatenateStrings("Local endpoint requested: ",realLocalEndpoint);
				log.info(logMessage);
				response = sendRequest(message, realLocalEndpoint);
			}else {
				if(match(SPARQL_REGEX, message.getRequest())) {
					response = virtualisationService.solveQuery(message);
				}else {
					log.severe("Endpoint requested was not found: "+message.getRequest());
				}
			}
		}catch(Exception e) {
			log.severe(e.toString());
		}
		return response;
	}
	
	private BridgingRule matchBridgingRoute(P2PMessage message, String method) {
		BridgingRule matchedRoute = null;
		List<BridgingRule> routes = bridgingService.getAllRoutes();

		int maxSize = routes.size();
		for(int index=0; index < maxSize; index++) {
			BridgingRule matchedRouteAux =  routes.get(index);
			if(match(matchedRouteAux.getXmppPattern(), message.getRequest()) && method.equalsIgnoreCase(matchedRouteAux.getMethod().toString())) {
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
	
	
	private Tuple<String,Integer> sendRequest(P2PMessage message, String endpoint){
		Tuple<String, Integer> requestResponse = PayloadsFactory.getErrorPayloadMethodRequestedNotAllowed();
		 Map<String,String> headersMap = retrieveHeaders(message.getHeaders());
		// Solve requests
		if (isGet(message)) {
			requestResponse = solveGetRequest(endpoint, headersMap);
		} else if(isPost(message)) {
			requestResponse = solvePostRequest(message, endpoint, headersMap);
		} else if(isDelete(message)) {
			requestResponse = solveDeleteRequest(message, endpoint, headersMap);
		} else if(isPut(message)) {
			requestResponse = solvePutRequest(message, endpoint, headersMap);
		} else if(isPatch(message)) {
			requestResponse = solvePatchRequest(message, endpoint, headersMap);
		}
		
		return requestResponse;
	}

	public Tuple<String,Integer> solveGetRequest(String endpoint, Map<String,String> headersMap){
		 Tuple<String,Integer> tuple = null;
		 try {
			 // Retrieve data using GET
			 tuple = new Tuple<>();
//			 System.out.println("----->>> Sending request to local endpoint, method GET:");
//			 System.out.println("----->>> \t endpoint:"+endpoint);
			 //headersMap.entrySet().stream().forEach(entry -> System.out.println("----->>> \t\t header:"+entry));
			 Unirest.setTimeouts(ConfigTokens.CONNECTION_TIMEOUT, ConfigTokens.SOKET_TIMEOUT);
			 HttpResponse<String> response = Unirest.get(endpoint).headers(headersMap).asString();
//			 System.out.println("----->>> - Response:"+response.getBody().length());
//			 System.out.println("----->>> - Response:"+response.getStatus());
//			 System.out.println("----->>> - Response:"+response.getStatusText());
			 tuple.setSecondElement(response.getStatus());
			 tuple.setFirstElement(response.getBody());
			 if(tuple.getSecondElement()>=200 && tuple.getSecondElement()<300 ) {
				 // Normalise payload if requited to Json-LD + Ontology
				 RDF normalisedData = virtualisationService.normalisePayload(tuple.getFirstElement(), endpoint, Method.GET.toString());
				 if(normalisedData!=null) {
					 tuple.setFirstElement(normalisedData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION));
					 ValidationReport report = validationService.generateValidationReport(normalisedData, endpoint);
					 if(report!=null) // means there was a validation error
						 tuple.setSecondElement(202);
				 }else {
					 tuple =  PayloadsFactory.getInteroperabilityErrorPayload();
				 }
			 }
		 }catch(Exception e) {
			 String logMessage = StringFactory.concatenateStrings("Remote endpoint'", endpoint, "' does not answered a GET request");
			 log.severe(logMessage);
			 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
		 }
		 return tuple;
	 }
	 
	 public Tuple<String, Integer> solvePostRequest(P2PMessage message, String endpoint, Map<String, String> headersMap) {
		 Tuple<String,Integer> tuple = PayloadsFactory.getInteroperabilityErrorPayload();
		 String requestBody = message.getMessage();
		 try {
			
			// Change normalised payload if requited into another understood by the endpoint
			 String specificEndpointPayload = virtualisationService.translatePayload(requestBody, message.getRequest());
			 if(specificEndpointPayload!=null) {
				 // Sending data using POST
				 tuple = new Tuple<>();
//				 System.out.println("----->>> Sending request to local endpoint, method POST:");
//				 System.out.println("----->>> \t endpoint:"+endpoint);
				 //headersMap.entrySet().stream().forEach(entry -> System.out.println("----->>> \t\t header:"+entry));
//				 System.out.println("----->>> \t payload:"+specificEndpointPayload);
				 Unirest.setTimeouts(ConfigTokens.CONNECTION_TIMEOUT, ConfigTokens.SOKET_TIMEOUT);
				 HttpResponse<String> response = Unirest.post(endpoint).headers(headersMap).body(specificEndpointPayload).asString();
//				 System.out.println("----->>> - Response:"+response.getBody());
//				 System.out.println("----->>> - Response:"+response.getStatus());
//				 System.out.println("----->>> - Response:"+response.getStatusText());
				 tuple.setSecondElement(response.getStatus());
				 tuple.setFirstElement(response.getBody());
				 // if response was correct try no normalised again the payload
				 if(tuple.getSecondElement()>200 && tuple.getSecondElement()<300 ) {
					 // Normalising answer payload if requited to Json-LD + Ontology
					 RDF normalisedData = virtualisationService.normalisePayload(response.getBody(), endpoint, message.getMethod());
					 if(normalisedData!=null) {
						 tuple.setFirstElement(normalisedData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION));
					 }else {
						 tuple =  PayloadsFactory.getInteroperabilityErrorPayload();
					 }
				 }
			 }else {
				 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
			 }
		 }catch(Exception e) {
			 String logMessage = StringFactory.concatenateStrings("Remote endpoint'", endpoint, "' does not answered a POST request");
			 log.severe(logMessage);
			 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
		 }
		 return tuple;
		}
	
	
	 private Tuple<String, Integer> solveDeleteRequest(P2PMessage message, String endpoint, Map<String, String> headersMap) {
		 Tuple<String,Integer> tuple = PayloadsFactory.getInteroperabilityErrorPayload();
		 String requestBody = message.getMessage();
		 try {
			
			// Change normalised payload if requited into another understood by the endpoint
			 String specificEndpointPayload = virtualisationService.translatePayload(requestBody, message.getRequest());
			 if(specificEndpointPayload!=null) {
				 // Sending data using POST
				 tuple = new Tuple<>();
//				 System.out.println("----->>> Sending request to local endpoint, method DELETE:");
//				 System.out.println("----->>> \t endpoint:"+endpoint);
				 //headersMap.entrySet().stream().forEach(entry -> System.out.println("----->>> \t\t header:"+entry));
//				 System.out.println("----->>> \t payload:"+specificEndpointPayload);
				 Unirest.setTimeouts(ConfigTokens.CONNECTION_TIMEOUT, ConfigTokens.SOKET_TIMEOUT);
				 HttpResponse<String> response = Unirest.delete(endpoint).headers(headersMap).body(specificEndpointPayload).asString();
//				 System.out.println("----->>> - Response:"+response.getBody());
//				 System.out.println("----->>> - Response:"+response.getStatus());
//				 System.out.println("----->>> - Response:"+response.getStatusText());
				 tuple.setSecondElement(response.getStatus());
				 tuple.setFirstElement(response.getBody());
				 // if response was correct try no normalised again the payload
				 if(tuple.getSecondElement()>200 && tuple.getSecondElement()<300 ) {
					 // Normalising answer payload if requited to Json-LD + Ontology
					 RDF normalisedData = virtualisationService.normalisePayload(response.getBody(), endpoint, message.getMethod());
					 if(normalisedData!=null) {
						 tuple.setFirstElement(normalisedData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION));
					 }else {
						 tuple =  PayloadsFactory.getInteroperabilityErrorPayload();
					 }
				 }
			 }else {
				 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
			 }
		 }catch(Exception e) {
			 String logMessage = StringFactory.concatenateStrings("Remote endpoint'", endpoint, "' does not answered a POST request");
			 log.severe(logMessage);
			 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
		 }
		 return tuple;
		}


		private Tuple<String, Integer> solvePutRequest(P2PMessage message, String endpoint, Map<String, String> headersMap) {
			 Tuple<String,Integer> tuple = PayloadsFactory.getInteroperabilityErrorPayload();
			 String requestBody = message.getMessage();
			 try {
				
				// Change normalised payload if requited into another understood by the endpoint
				 String specificEndpointPayload = virtualisationService.translatePayload(requestBody, message.getRequest());
				 if(specificEndpointPayload!=null) {
					 // Sending data using POST
					 tuple = new Tuple<>();
//					 System.out.println("----->>> Sending request to local endpoint, method PUT:");
//					 System.out.println("----->>> \t endpoint:"+endpoint);
					 //headersMap.entrySet().stream().forEach(entry -> System.out.println("----->>> \t\t header:"+entry));
//					 System.out.println("----->>> \t payload:"+specificEndpointPayload);
					 Unirest.setTimeouts(ConfigTokens.CONNECTION_TIMEOUT, ConfigTokens.SOKET_TIMEOUT);
					 HttpResponse<String> response = Unirest.put(endpoint).headers(headersMap).body(specificEndpointPayload).asString();
//					 System.out.println("----->>> - Response:"+response.getBody());
//					 System.out.println("----->>> - Response:"+response.getStatus());
//					 System.out.println("----->>> - Response:"+response.getStatusText());
					 tuple.setSecondElement(response.getStatus());
					 tuple.setFirstElement(response.getBody());
					 // if response was correct try no normalised again the payload
					 if(tuple.getSecondElement()>200 && tuple.getSecondElement()<300 ) {
						 // Normalising answer payload if requited to Json-LD + Ontology
						 RDF normalisedData = virtualisationService.normalisePayload(response.getBody(), endpoint, message.getMethod());
						 if(normalisedData!=null) {
							 tuple.setFirstElement(normalisedData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION));
						 }else {
							 tuple =  PayloadsFactory.getInteroperabilityErrorPayload();
						 }
					 }
				 }else {
					 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
				 }
			 }catch(Exception e) {
				 String logMessage = StringFactory.concatenateStrings("Remote endpoint'", endpoint, "' does not answered a POST request");
				 log.severe(logMessage);
				 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
			 }
			 return tuple;
		}
	 
		private Tuple<String, Integer> solvePatchRequest(P2PMessage message, String endpoint,
				Map<String, String> headersMap) {
			 Tuple<String,Integer> tuple = PayloadsFactory.getInteroperabilityErrorPayload();
			 String requestBody = message.getMessage();
			 try {
				
				// Change normalised payload if requited into another understood by the endpoint
				 String specificEndpointPayload = virtualisationService.translatePayload(requestBody, message.getRequest());
				 if(specificEndpointPayload!=null) {
					 // Sending data using POST
					 tuple = new Tuple<>();
//					 System.out.println("----->>> Sending request to local endpoint, method PATCH:");
//					 System.out.println("----->>> \t endpoint:"+endpoint);
					 //headersMap.entrySet().stream().forEach(entry -> System.out.println("----->>> \t\t header:"+entry));
//					 System.out.println("----->>> \t payload:"+specificEndpointPayload);
					 Unirest.setTimeouts(ConfigTokens.CONNECTION_TIMEOUT, ConfigTokens.SOKET_TIMEOUT);
					 HttpResponse<String> response = Unirest.patch(endpoint).headers(headersMap).body(specificEndpointPayload).asString();
//					 System.out.println("----->>> - Response:"+response.getBody());
//					 System.out.println("----->>> - Response:"+response.getStatus());
//					 System.out.println("----->>> - Response:"+response.getStatusText());
					 tuple.setSecondElement(response.getStatus());
					 tuple.setFirstElement(response.getBody());
					 // if response was correct try no normalised again the payload
					 if(tuple.getSecondElement()>200 && tuple.getSecondElement()<300 ) {
						 // Normalising answer payload if requited to Json-LD + Ontology
						 RDF normalisedData = virtualisationService.normalisePayload(response.getBody(), endpoint, message.getMethod());
						 if(normalisedData!=null) {
							 tuple.setFirstElement(normalisedData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION));
						 }else {
							 tuple =  PayloadsFactory.getInteroperabilityErrorPayload();
						 }
					 }
				 }else {
					 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
				 }
			 }catch(Exception e) {
				 String logMessage = StringFactory.concatenateStrings("Remote endpoint'", endpoint, "' does not answered a POST request");
				 log.severe(logMessage);
				 tuple = PayloadsFactory.getErrorPayloadRemoteEndpointDown();
			 }
			 return tuple;
		}
		
	// --- Ancillary methods
	
	private Boolean isGet(P2PMessage message) {
		return message.getMethod().trim().equalsIgnoreCase(Method.GET.toString());
	}
	
	private Boolean isPost(P2PMessage message) {
		return message.getMethod().trim().equalsIgnoreCase(Method.POST.toString());
	}
	
	private boolean isPatch(P2PMessage message) {
		return  message.getMethod().trim().equalsIgnoreCase(Method.PATCH.toString());
	}
	
	private boolean isDelete(P2PMessage message) {
		return  message.getMethod().trim().equalsIgnoreCase(Method.DELETE.toString());
	}
	
	private boolean isPut(P2PMessage message) {
		return  message.getMethod().trim().equalsIgnoreCase(Method.PUT.toString());
	}
	
	public Map<String,String> retrieveHeaders(String headersStr){
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
			log.severe(e.toString());
			throw new IllegalArgumentException("Error processing headers for POST request");
		}
		return headers;
	}
}
