package cim.xmpp.factory;

import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cim.ConfigTokens;
import cim.model.P2PMessage;
import cim.service.XMPPService;


public class P2PMessageFactory {
	
	// -- Attributes
	private static Logger log = Logger.getLogger(P2PMessageFactory.class.getName());
	
	// static P2P Message tokens
	private static final String SLASH_TOKEN = "/";
	private static final String OWNER_TOKEN = "owner";
	private static final String EMPTY_TOKEN = "";


	// -- Constructor 
	private P2PMessageFactory() {
		// empty
	}
	
	// -- Methods
	
	public static P2PMessage createP2PRequestMessage(HttpServletRequest request, Map<String, String> headers)  {
		 P2PMessage p2pMessage = new P2PMessage();
		 Date now = new Date();
		 // Compute p2pMessage fields
		 String remotePath = retrievePath(request);
		 String method = request.getMethod();
		 String parameters = buildParameters(request);
		 String remoteRequest = remotePath.substring(remotePath.indexOf(SLASH_TOKEN))+parameters;
		 // __ compute the receiver id
		 String receiverId = computeP2PMessageReceiver(remotePath);
		 // __ compute the message id 
		 String documentId = computeP2PMessageId(XMPPService.getCurrentXmppUser(), receiverId, now);
		 // __init the p2pMessage
		 p2pMessage.setId(documentId);
		 p2pMessage.setOwner(XMPPService.getCurrentXmppUser());
		 p2pMessage.setReceiver(receiverId);
		 p2pMessage.setTime(now.toString());
		 p2pMessage.setRequest(remoteRequest);
		 p2pMessage.setMessage(EMPTY_TOKEN);
		 p2pMessage.setMethod(method);
		 p2pMessage.setError(false);
		 // TODO: Add headers
		 return p2pMessage;
	}
	
	private static String buildParameters(HttpServletRequest request) {
		String result = "";
		try {
			StringBuilder parameters = new StringBuilder("?");
			Enumeration<String> parameterNames = request.getParameterNames();
			while(parameterNames.hasMoreElements()) {
				String parameter = parameterNames.nextElement();
				String parameterValue = URLEncoder.encode(request.getParameter(parameter), "UTF-8");
				parameters.append(parameter).append("=").append(parameterValue).append("&");
			}
			if(parameters.length()>1)
				result = parameters.substring(0, parameters.lastIndexOf("&"));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	
	private static String computeP2PMessageId(String owner, String receiver, Date now) {
		 StringBuilder documentId = new StringBuilder(owner);
		 documentId.append("::").append(receiver).append("::").append(now.toString());
		 return documentId.toString();
	}
	
	private static String computeP2PMessageReceiver(String remotePath) {
		String owner = remotePath.substring(0, remotePath.indexOf(SLASH_TOKEN)).toLowerCase();
		 StringBuilder receiverId = new StringBuilder(owner);
		 receiverId.append("@").append(XMPPService.p2pDomain);
		 return receiverId.toString();
	}
	 private static String retrievePath(HttpServletRequest request) {
		 String urlToken = ConfigTokens.URL_TOKEN;
		 String path = request.getRequestURL().toString();
		 path = path.substring(path.indexOf("://")+3, path.length());
		 String remotePath = path.substring(path.indexOf(urlToken)+urlToken.length());
		 if(remotePath.startsWith(SLASH_TOKEN))
			 remotePath = remotePath.substring(1);
		 return remotePath;
	 }
	 
	 public static P2PMessage createP2PMessageFromJson(String jsonDocumentEnconded) {
		 String jsonDocument = "";
		 
		 try {
			 jsonDocument = new String(Base64.getDecoder().decode(jsonDocumentEnconded.getBytes()), "UTF-8");
			 System.out.println(">>>>>"+jsonDocument);
		 	}catch(Exception e) {
		 		System.out.println("Error processing message "+jsonDocumentEnconded);
			 }
		 P2PMessage p2pMessage = new P2PMessage();
		 try {
		 JsonParser parser = new JsonParser();
		 
		 JsonObject jsonP2PMessage = parser.parse(jsonDocument).getAsJsonObject();
		 if(jsonP2PMessage.has("id")) 
			 p2pMessage.setId(jsonP2PMessage.get("id").getAsString());
		 if(jsonP2PMessage.has(OWNER_TOKEN)) 
			 p2pMessage.setOwner(jsonP2PMessage.get(OWNER_TOKEN).getAsString());
		 if(jsonP2PMessage.has("receiver")) 
			 p2pMessage.setReceiver(jsonP2PMessage.get("receiver").getAsString());
		 if(jsonP2PMessage.has("time")) 
			 p2pMessage.setTime(jsonP2PMessage.get("time").getAsString());
		 if(jsonP2PMessage.has("request")) 
			 p2pMessage.setRequest(jsonP2PMessage.get("request").getAsString());
		 if(jsonP2PMessage.has("message")) 
			 p2pMessage.setMessage(jsonP2PMessage.get("message").getAsString());
		 if(jsonP2PMessage.has("method")) 
			 p2pMessage.setMethod(jsonP2PMessage.get("method").getAsString());
		 if(jsonP2PMessage.has("error")) 
			 p2pMessage.setError(jsonP2PMessage.get("error").getAsBoolean());
		 }catch(Exception e) {
			 log.severe("Errors raised when transforming into P2PMessage the following json: "+jsonDocument);
			 log.severe(jsonDocument);
			 e.printStackTrace();
		 }
		 return p2pMessage;
	}
	 
	 public String fromP2PMessageToJSon(P2PMessage message) {
		 	JsonObject jsonMessage = new JsonObject();
		 	jsonMessage.addProperty("id", message.getId());
		 	jsonMessage.addProperty(OWNER_TOKEN, message.getOwner());
		 	jsonMessage.addProperty("receiver", message.getReceiver());
		 	jsonMessage.addProperty("time", message.getTime());
		 	jsonMessage.addProperty("request", message.getRequest());
		 	jsonMessage.addProperty("message", message.getMessage());
		 	jsonMessage.addProperty("method", message.getMethod());
		 	jsonMessage.addProperty("error", message.getError());
		    // TODO: Add headers
			return jsonMessage.toString();
	 }
	 
	 // TODO: REPLACE THE BASE64 FOR ENCRIPTION 
	 public static String fromP2PMessageToB64(P2PMessage message) {
		 	JsonObject jsonMessage = new JsonObject();
		 	jsonMessage.addProperty("id", message.getId());
		 	jsonMessage.addProperty(OWNER_TOKEN, message.getOwner());
		 	jsonMessage.addProperty("receiver", message.getReceiver());
		 	jsonMessage.addProperty("time", message.getTime());
		 	jsonMessage.addProperty("request", message.getRequest());
		 	jsonMessage.addProperty("message", message.getMessage());
		 	jsonMessage.addProperty("method", message.getMethod());
		 	jsonMessage.addProperty("error", message.getError());
		    // TODO: Add headers
			return Base64.getEncoder().encodeToString(jsonMessage.toString().getBytes());
	 }
	 
	 public static P2PMessage createP2PMessage(String owner, String receiver, String message) {
		 P2PMessage p2pMessage = new P2PMessage();
		 Date now = new Date();
		 p2pMessage.setOwner(owner);
		 p2pMessage.setReceiver(receiver);
		 p2pMessage.setId(computeP2PMessageId(owner, receiver, now));
		 p2pMessage.setTime(now.toString());
		 p2pMessage.setMessage(message);
		 p2pMessage.setRequest(EMPTY_TOKEN);
		 p2pMessage.setMethod(EMPTY_TOKEN);

		 return p2pMessage;
	 }

	
}

