package cim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import cim.ConfigTokens;
import cim.model.Route;



@SpringBootApplication // This prevents Spring to create default error-handling route /error  (exclude = ErrorMvcAutoConfiguration.class )
@EnableAsync
@EnableScheduling
public class DeltaCimApplication {

	// -- Attributes 
	// Log
	private static Logger log = Logger.getLogger(DeltaCimApplication.class.getName());
	// Re-routing
	private static List<Route> routes = new CopyOnWriteArrayList<>();
	// Xmpp config
	private static String username = ConfigTokens.P2P_CONFIG_USERNAME;
	private static String xmppDomain = ConfigTokens.P2P_CONFIG_XMPP_DOMAIN;
	private static String password = ConfigTokens.P2P_CONFIG_PASS;
	private static String host = ConfigTokens.P2P_CONFIG_HOST;
	
	private static Integer port = Integer.parseInt(ConfigTokens.P2P_CONFIG_PORT);
	
	// -- Startup method
	
	public static void main(String[] args) {
		// Read file setup
		for(int index=0; index<args.length;index++) {
			String arg = args[index];
			if(arg.startsWith(ConfigTokens.P2P_FILE_ARGUMENT)) {
				String fileDir = arg.replace(ConfigTokens.P2P_FILE_ARGUMENT, "").trim();
				readSetupFile(fileDir);
			}
		}
		// Check that there was no error, this will quit the program in case an error happened
		checkErrors();
		// Initialize Spring APP
		SpringApplication.run(DeltaCimApplication.class, args);
	}
	
	/**
	 * This method checks that the configuration file provided has the mandatory keys in order to correctly setup the p2p client
	 */
	private static void checkErrors() {
		if(username==null)
			log.severe("Provided configuration lacks of mandatory field 'username'");
		if(password==null)
			log.severe("Provided configuration lacks of mandatory field 'password'");
		if(host==null)
			log.severe("Provided configuration lacks of mandatory field 'host'");
		if(xmppDomain==null)
			log.severe("Provided configuration lacks of mandatory field 'xmpp_domain'");
		if(port==null)
			log.severe("Provided configuration lacks of mandatory field 'port'");
		if(username==null || password==null || host==null || xmppDomain==null || port==null) {
			System.exit(-1);
		}
	}

	/**
	 * This method initializes the static fields required by the p2p client to work
	 * @param fileDir A file directory under which the required p2p fields were specified
	 */
	private static void readSetupFile(String fileDir) {
		try {
			String fileContent = readFile(fileDir);
			JsonParser parser = new JsonParser();
			JsonObject configFile = parser.parse(fileContent).getAsJsonObject();
			if(configFile.has(ConfigTokens.P2P_CONFIG_USERNAME))
				username = configFile.get(ConfigTokens.P2P_CONFIG_USERNAME).getAsString();
			if(configFile.has(ConfigTokens.P2P_CONFIG_PASS))
				password = configFile.get(ConfigTokens.P2P_CONFIG_PASS).getAsString();
			if(configFile.has(ConfigTokens.P2P_CONFIG_HOST))
				host = configFile.get(ConfigTokens.P2P_CONFIG_HOST).getAsString();
			if(configFile.has(ConfigTokens.P2P_CONFIG_XMPP_DOMAIN))
				xmppDomain = configFile.get(ConfigTokens.P2P_CONFIG_XMPP_DOMAIN).getAsString();
			if(configFile.has(ConfigTokens.P2P_CONFIG_PORT))
				port = configFile.get(ConfigTokens.P2P_CONFIG_PORT).getAsInt();
			if(configFile.has(ConfigTokens.P2P_CONFIG_ROUTES))
				storeRoutes(configFile.get(ConfigTokens.P2P_CONFIG_ROUTES).getAsJsonArray() );
		}catch (Exception e) {
			log.severe("Provided Json p2p configuration file has syntax errors");
			System.exit(-1);
		}
	}
	
	private static void storeRoutes(JsonArray jsonArray) {
		int maxSize = jsonArray.size();
		for(int index=0; index < maxSize; index++) {
			JsonObject jsonRoute = jsonArray.get(index).getAsJsonObject();
			if(jsonRoute.has(ConfigTokens.P2P_CONFIG_ROUTE_ENDPOINT) && jsonRoute.has(ConfigTokens.P2P_CONFIG_ROUTE_REGEX)) {
				String regex = jsonRoute.get(ConfigTokens.P2P_CONFIG_ROUTE_REGEX).getAsString();
				String endpoint = jsonRoute.get(ConfigTokens.P2P_CONFIG_ROUTE_ENDPOINT).getAsString();
				Boolean append = false;
				if(jsonRoute.has(ConfigTokens.P2P_CONFIG_ROUTE_APPEND))
					append = jsonRoute.get(ConfigTokens.P2P_CONFIG_ROUTE_APPEND).getAsBoolean();
				Route route = new Route();
				route.setEndpoint(endpoint);
				route.setRegexPath(regex);
				route.setAppendPath(append);
				routes.add(route);
			}else {
				log.severe("Provided Json p2p configuration file has syntax errors in the routes array, some mandatory key is missing");
				System.exit(-1);
			}
		}
	}
	
	/**
	 * This method reads the content of a file
	 * @param fileName A directory specifying a file
	 * @return This method returns the content of the file
	 */
	 private static String readFile(String fileName) {
		 StringBuilder data = new StringBuilder();
		 FileReader file = null;
		 BufferedReader bf =null;
			// 1. Read the file
			try {
				file = new FileReader(fileName);
				bf = new BufferedReader(file);
				// 2. Accumulate its lines in the data var
				bf.lines().forEach( line -> data.append(line).append("\n"));
				
			}catch(Exception e) {
				log.severe(e.toString());
				System.exit(-1);
			} finally {
				try {
					if(bf!=null)
						bf.close();
					if(file!=null)
						file.close();	
				} catch (IOException e) {
					log.severe(e.toString());
					System.exit(-1);
				}
			}
			return data.toString();
	 }

	// -- Static methods
		
	public static String getUsername() {
		return username;
	}

	public static String getXmppDomain() {
		return xmppDomain;
	}
	
	public static List<Route> getRoutes() {
		return routes;
	}

	public static String getPassword() {
		return password;
	}

	public static String getHost() {
		return host;
	}

	public static Integer getPort() {
		return port;
	}

	
	
}
