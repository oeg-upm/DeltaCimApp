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
	
	private static final String ERROR_JSON_MESSAGES_CIM_UNAUTHORISED = "{\n  \"error\": {\n    \"code\": 401,\n    \"message\": \"Error, your CIM is not authorized by remote CIM\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_REQUEST_ERROR = "{\n  \"error\": {\n    \"code\": 409,\n    \"message\": \"Error in the request\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_REMOTE_ROUTE_NOT_FOUND = "{\n  \"error\": {\n    \"code\": 404,\n    \"message\": \"Remote endpoint was not found\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_METHOD_NOT_ALLOWED = "{\n  \"error\": {\n    \"code\": 405,\n    \"message\": \"Requested method is not supported, currently CIM supports only GET and POST\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_REMOTE_ENDPOINT_DOWN = "{\n  \"error\": {\n    \"code\": 404,\n    \"message\": \"Requested endpoint seems to be down since does not respond\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_QUERY_HAS_SYNTAX_ERRORS = "{\n  \"error\": {\n    \"code\": 400,\n    \"message\": \"Query provided has syntax errors\"\n  }\n }";
	public static final String ERROR_JSON_MESSAGES_QUERY_TYPE_NOT_SUPPORTED = "{\n  \"error\": {\n    \"code\": 405,\n    \"message\": \"Query provided is not supported, currently only SELECT, ASK, DESCRIBE, and CONSTRUCT queries are supported\"\n  }\n }";
	private static final String ERROR_JSON_MESSAGES_CIM_UNAUTHORISED_SENDING_DATA = "{\n  \"error\": {\n    \"code\": 401,\n    \"message\": \"Error, your CIM is not authorized to send data to the remote CIM\"\n  }\n }";

	public static Tuple<String,Integer> getUnauthorisedCIMErrorPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_CIM_UNAUTHORISED, HttpServletResponse.SC_UNAUTHORIZED);
	}
	
	public static Tuple<String,Integer> getRequestErrorPayload() {
		return new Tuple<>(ERROR_JSON_MESSAGES_REQUEST_ERROR, HttpServletResponse.SC_CONFLICT);
	}

	public static Tuple<String,Integer> getErrorPayloadRemoteRouteDoesNotExists() {
		return new Tuple<>(ERROR_JSON_MESSAGES_REMOTE_ROUTE_NOT_FOUND, HttpServletResponse.SC_NOT_FOUND);
	}
	
	
	public static Tuple<String,Integer> getErrorPayloadMethodRequestedNotAllowed() {
		return new Tuple<>(ERROR_JSON_MESSAGES_METHOD_NOT_ALLOWED, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	public static Tuple<String,Integer> getErrorPayloadRemoteEndpointDown() {
		return new Tuple<>(ERROR_JSON_MESSAGES_REMOTE_ENDPOINT_DOWN, HttpServletResponse.SC_NOT_FOUND);
	}

	public static Tuple<String, Integer> getErrorPayloadSyntaxQueryErrors() {
		return  new Tuple<>(ERROR_JSON_MESSAGES_QUERY_HAS_SYNTAX_ERRORS, HttpServletResponse.SC_BAD_REQUEST);
	}

	public static Tuple<String, Integer> getErrorPayloadQueryNotSupported() {
		return  new Tuple<>(ERROR_JSON_MESSAGES_QUERY_TYPE_NOT_SUPPORTED, HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	public static Tuple<String, Integer> getUnauthorisedCIMErrorPayloadSending() {
		return new Tuple<>(ERROR_JSON_MESSAGES_CIM_UNAUTHORISED_SENDING_DATA, HttpServletResponse.SC_UNAUTHORIZED);
	}
	

	
}
