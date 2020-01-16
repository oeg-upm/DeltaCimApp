package cim;

public class ConfigTokens {

	// Input arguments
	public static final String P2P_FILE_ARGUMENT = "--server.p2p=";
	
	// Config file tokens
	public static final String P2P_CONFIG_USERNAME = "jcano";
	public static final String P2P_CONFIG_PASS = "Lotus123";
	public static final String P2P_CONFIG_HOST = "jcano.ddns.net";
	public static final String P2P_CONFIG_XMPP_DOMAIN = "jcano.ddns.net";
	public static final String P2P_CONFIG_PORT = "5222";
	public static final String P2P_CONFIG_ROUTES = "routes";
	public static final String P2P_CONFIG_ROUTE_REGEX = "regex";
	public static final String P2P_CONFIG_ROUTE_ENDPOINT = "endpoint";
	public static final String P2P_CONFIG_ROUTE_APPEND = "append";
	public static final String P2P_CONFIG_CACERT_FOLDER = "./Certificates/cacert";
	public static final String VALIDATIONS_SHAPES_FILE = "./shapes/delta-shapes.ttl";
	public static final String VALIDATIONS_SHAPES_SUCCESS_MESSAGE  = "Successfully validated!";
	// URL token identifier
	public static final  String URL_TOKEN = "/delta"; 

	
	public static final String ERROR_JSON_MESSAGES_1 = "{\n" + 
			"  \"error\": {\n" + 
			"    \"code\": 409,\n" + 
			"    \"message\": \"Error in the request\"\n" + 
			"  }\n" + 
			"}";
	
	public static final String ERROR_JSON_MESSAGES_2 = "{\n" + 
			"  \"error\": {\n" + 
			"    \"code\": 409,\n" + 
			"    \"message\": \"Error, message received has no content\"\n" + 
			"  }\n" + 
			"}";

}
