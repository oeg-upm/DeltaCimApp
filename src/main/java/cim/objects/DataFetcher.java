package cim.objects;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import cim.DeltaCimApplication;
import cim.model.P2PMessage;
import cim.model.Route;

public class DataFetcher {

	private Logger log = Logger.getLogger(DataFetcher.class.getName());
	
	public String fetchData(P2PMessage message) {
		String response =  null;
		String endpoint = buildEndpointRoute(message);
		System.out.println("Local endpoint requested: "+endpoint);
		if(endpoint!=null)
			response= sendRequest(message, endpoint);
		if(response==null){
			response = "{\"error\":\"internal error\"}";
			log.severe("Endpoint requested was not found: "+endpoint);
		}
		return response;
	}
	


	private String buildEndpointRoute(P2PMessage message) {
		String endpointRoute = null;
		int maxSize = DeltaCimApplication.getRoutes().size();
		for(int index=0; index < maxSize; index++) {
			Route route =  DeltaCimApplication.getRoutes().get(index);
			if(match(route.getRegexPath(), message.getRequest())) {
				endpointRoute = route.getEndpoint();
				String path = message.getRequest();
				endpointRoute = buildEndpointPath(endpointRoute, path, route.getAppendPath()); 
			}
		}
		return endpointRoute;
	}
	
	private String buildEndpointPath(String endpoint, String path, Boolean append) {
		String endpointRoute = endpoint;
		if(append) {
			if((!endpointRoute.endsWith("/") && path.startsWith("/")) || (endpointRoute.endsWith("/") && !path.startsWith("/"))) {
				endpointRoute = endpointRoute.concat(path);
			}else if(!endpointRoute.endsWith("/") && !path.startsWith("/")) {
				endpointRoute = endpointRoute.concat("/"+path);
			}else if(endpointRoute.endsWith("/") && path.startsWith("/")) {
				endpointRoute = endpointRoute.concat(path.replaceFirst("/", ""));
			}
		}
		return endpointRoute;
	}
	
	private boolean match(String pattern,String value) {
		Boolean match = false; 
		Pattern reegexPattern = Pattern.compile(pattern);
	    Matcher matcher = reegexPattern.matcher(value);
	    if (matcher.find()) 
	    		match = true;
		return match;
	}
	
	
	private String sendRequest(P2PMessage message, String endpoint) {
		String methodNormalized = message.getMethod().trim().toLowerCase();
		String responseMessage = null;
		try {
			if(methodNormalized.equals("get")) {
				responseMessage = Unirest.get(endpoint).asString().getBody();
			}else if(methodNormalized.equals("post")) {
				responseMessage = Unirest.post(endpoint).body(message.getMessage()).asString().getBody();
			}else if(methodNormalized.equals("put")) {
				//responseMessage = Unirest.put(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("PUT requests not implemented yet");
			}else if(methodNormalized.equals("delete")) {
				//responseMessage = Unirest.delete(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("DELETE requests not implemented yet");
			}else if(methodNormalized.equals("patch")) {
				//responseMessage = Unirest.patch(endpoint).body(message.getMessage()).asString().getBody();
				System.out.println("PATCH requests not implemented yet");
			}
		} catch (UnirestException e) {
			e.printStackTrace();
			System.out.println(endpoint);
		}
		return responseMessage;
	}
}
