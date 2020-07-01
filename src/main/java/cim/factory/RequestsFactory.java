package cim.factory;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;

import cim.model.BridgingRule;
import cim.model.P2PMessage;

public class RequestsFactory {

	
	
	public static  String buildRealLocalEndpoint(P2PMessage message, BridgingRule route) {
		String endpointRoute = route.getEndpoint();
		String path = message.getRequest();
		return buildEndpointPath(endpointRoute, path, route.getAppendPath()); 
	}
	
	
	private static String buildEndpointPath(String endpoint, String path, Boolean append) {
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
	
	
	
	
	@SuppressWarnings("rawtypes")
	public static Map<String, String> extractHeaders(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		Enumeration headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String key = (String) headerNames.nextElement();
			String value = request.getHeader(key);
			map.put(key, value);
		}
		return map;
	}
	
	public static String extractHeadersToString(HttpServletRequest request) {
		Map<String, String> headers = extractHeaders(request);
		return fromHeadersMaptoString(headers);
	}
	
	public static String fromHeadersMaptoString( Map<String, String> headers) {
		JsonObject jsonHeaders = new JsonObject();
		headers.entrySet().stream().forEach(entry -> jsonHeaders.addProperty(entry.getKey(), entry.getValue()));
		jsonHeaders.remove("host"); // Otherwise the CIM that has to forward this header, and is not in the same host, will fail
		return jsonHeaders.toString();
	}
}
