package cim.service;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.Unirest;

import cim.ConfigTokens;
import helio.framework.objects.Tuple;

@Service
public class CloudService extends VirtualisationService{


	@Autowired
	public ACLService aclService;
	
	
	private Logger log = Logger.getLogger(CloudService.class.getName());

	public CloudService() {
		//empty
	}

	public Tuple<String, Integer> federateQuery(String queryString, Map<String,String> headers, String token) {
		JsonObject answer = new JsonObject();
		JsonObject header = null;
		JsonObject results = new JsonObject();
		JsonArray bindingResults = new JsonArray();
		JsonParser parser = new JsonParser();
		queryString = this.cleanQuery(queryString);
		Set<String> endpoints = aclService.getAllXmppUsernames().stream().map(user -> transformToDELTAURLs(user)).collect(Collectors.toSet());
		for(String endpoint:endpoints) {
			try {
				Unirest.setTimeouts(60000, 60000);
				endpoint = endpoint+"?query="+URLEncoder.encode(queryString, "UTF-8");
				String responseMessage = Unirest.get(endpoint).header("Authorization", token).asString().getBody();
				JsonObject jsonPartialResult = parser.parse(responseMessage).getAsJsonObject();
				if(header ==null) {
					header = jsonPartialResult.getAsJsonObject("head");
				}
				JsonArray bindingResultsMessage = jsonPartialResult.getAsJsonObject("results").getAsJsonArray("bindings");
				bindingResults.addAll(bindingResultsMessage);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(header!=null) {
			results.add("bindings", bindingResults);
		}else{
			results.add("bindings", new JsonArray());
			header = new JsonObject();
		}
		answer.add("head", header);
		answer.add("results", results);

		return new Tuple<String,Integer>(answer.toString(),200);
	}


	

	private String transformToDELTAURLs(String user) {
		StringBuilder formatedEndpoint= new StringBuilder();
		formatedEndpoint.append("http://").append("localhost:").append(ConfigTokens.SERVER_PORT).append("/delta/").append(user).append("/sparql").append("");
		return formatedEndpoint.toString();
	}

	
	
	
	private String rewriteQuery(String queryString, List<String> users) {
		Set<String> endpoints = users.stream().map(user -> transformToDELTAURLs(user)).collect(Collectors.toSet());
		// TODO: perform discovery over the endpoints
		String endpointsQueryFragment =  buildQueryServiceToken(endpoints); 
		queryString = queryString.replaceFirst("\\{", "{\n\tSERVICE ?service {");
		int lastIndex = queryString.lastIndexOf("}");
		String rewrittenQuery = queryString.substring(0, lastIndex)+endpointsQueryFragment+queryString.substring(lastIndex, queryString.length());
		System.out.println(rewrittenQuery);
		return rewrittenQuery;
	}
	
	private String buildQueryServiceToken(Set<String> endpoints) {
		StringBuilder fragment = new StringBuilder();
		fragment.append("\n\t} VALUES ?service { ");
		endpoints.forEach(endpoint -> fragment.append(endpoint).append(" "));
		fragment.append("}\n");
		return fragment.toString();
	}

	



}
