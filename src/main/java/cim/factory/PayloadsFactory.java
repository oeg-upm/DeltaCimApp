package cim.factory;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

import cim.model.enums.ConnectionStatus;
import helio.framework.objects.Tuple;

public class PayloadsFactory {

	private static final String ERROR_JSON_MESSAGES_CIM_DISCONNECTEED = "{\n  \"error\": {\n    \"message\": \"CIM is currently disconnected\"\n  }\n}";
	private static final String ERROR_JSON_MESSAGES_INTEROPERABILITY = "{\n  \"error\": {\n    \"message\": \"Payload is not interoperable\"\n  }\n}";

	
	private PayloadsFactory() {
		// empty
	}
	
	public static Tuple<String,Integer> getCIMDisconnectedPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_CIM_DISCONNECTEED, HttpServletResponse.SC_CONFLICT);
	}
	
	public static String getXmppConnectionStatusPayload(String user, Boolean isConnected) {
		JsonObject payload = new JsonObject();
		JsonObject xmpp = new JsonObject();
		xmpp.addProperty("is_connected",isConnected);
		xmpp.addProperty("xmpp_user", user);
		payload.add("xmpp", xmpp);
		return payload.toString();
	}
	
	public static String getXmppConnectPayload(Boolean isConnected, String user, ConnectionStatus status) {
		JsonObject payload = new JsonObject();
		JsonObject xmpp = new JsonObject();
		xmpp.addProperty("is_connected",isConnected);
		xmpp.addProperty("xmpp_user", user);
		xmpp.addProperty("message",  status.toString());
		payload.add("xmpp", xmpp);
		return payload.toString();
	}

	public static Tuple<String, Integer> getInteroperabilityErrorPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_INTEROPERABILITY, 418);
	}
	
	// Payloads for xmpp message received
	
	private static final String ERROR_JSON_MESSAGES_CIM_UNAUTHORISED = "{\n  \"error\": {\n    \"code\": 403,\n    \"message\": \"Error, your CIM is not authorized by remote CIM\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_REQUEST_ERROR = "{\n  \"error\": {\n    \"code\": 409,\n    \"message\": \"Error in the request\"\n  }\n }";
	
	public static Tuple<String,Integer> getUnauthorisedCIMErrorPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_CIM_UNAUTHORISED, HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	public static Tuple<String,Integer> getRequestErrorPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_REQUEST_ERROR, HttpServletResponse.SC_CONFLICT);
	}

	

	
}
