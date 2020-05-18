package cim;

import org.apache.jena.riot.Lang;

public class ConfigTokens {

	
	// Default setup
	public static final String DEFAULT_USER = "default";
	public static final String DEFAULT_PASSWORD = "default";
	public static final String DEFAULT_DOMAIN = "jcano.ddns.net";
	public static final String DEFAULT_HOST = "jcano.ddns.net";
	public static final String DEFAULT_PORT = "5222";
	public static final String P2P_CONFIG_CACERT_FOLDER = "./Certificates/cacerts";
	public static final String P2P_CONFIG_MUTAL_AUTH__FOLDER = "./Certificates/upm_cert.pem";
	public static final String VALIDATIONS_SHAPES_FILE = "./shapes/delta-shapes.ttl";
	public static final String VALIDATIONS_SHAPES_SUCCESS_MESSAGE  = "Successfully validated!";
	public static final String MODULES_FOLDER = "./modules";
	public static final String MODULES_BASE_DIR = "./modules/";
	public static final String MODULES_FILE_EXTENSION = ".module";
	public static final String MODULES_BASE_DIR_READING = "/reading/";
	public static final String MODULES_BASE_DIR_WRITTING = "/writting/";
	
	public static String LOCAL_PORT = "8080";
	public static String PASSWORD_CERT = "changeit";
	
	// URL token identifier
	public static final  String URL_TOKEN = "/delta"; 
	
	
	// Syntactic interoperability by defaul
	public static final String DEFAULT_RDF_SERIALISATION = Lang.JSONLD.getLabel();
	
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


	public static final String ERROR_JSON_MESSAGES_3 = "{\n" + 
			"  \"error\": {\n" + 
			"    \"code\": 401,\n" + 
			"    \"message\": \"Error, unauthorized\"\n" + 
			"  }\n" + 
			"}";
	
	public static final String ERROR_JSON_MESSAGES_4 = "{\n" + 
			"  \"error\": {\n" + 
			"    \"code\": 418,\n" + 
			"    \"message\": \"Remote service provided incorrect data\"\n" + 
			"  }\n" + 
			"}";
	
	public static final String ERROR_JSON_MESSAGES_5= "{\n" + 
			"  \"error\": {\n" + 
			"    \"code\": 418,\n" + 
			"    \"message\": \"Posted data is not interoperable\"\n" + 
			"  }\n" + 
			"}";
	

}
