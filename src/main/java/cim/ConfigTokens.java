package cim;

public class ConfigTokens {

	
	// Default setup
	public static final String DEFAULT_DOMAIN = "jcano.ddns.net";
	public static final String DEFAULT_HOST = "jcano.ddns.net";
	public static final String P2P_CONFIG_CACERT_FOLDER = "./Certificates/cacerts";
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
