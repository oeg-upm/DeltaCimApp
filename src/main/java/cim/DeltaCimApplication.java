package cim;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import cim.model.enums.ConnectionStatus;
import cim.service.UserService;
import cim.service.ValidationService;
import cim.service.XMPPService;

@SpringBootApplication // This prevents Spring to create default error-handling route /error  (exclude = ErrorMvcAutoConfiguration.class )
@EnableAsync
@EnableAutoConfiguration
public class DeltaCimApplication {
	
	@Autowired
	public Environment environment;
	@Autowired
	public UserService userService;
	@Autowired
	public XMPPService xmppService;
	@Autowired
	public ValidationService validationService;

	private static Logger log = Logger.getLogger(DeltaCimApplication.class.getName());


	public static void main(String[] args) {
		SpringApplication.run(DeltaCimApplication.class, args);
	}
	
	
	@PostConstruct
	public void initLocalPort() {
		ConfigTokens.SERVER_PORT = environment.getProperty("server.port");
		ConfigTokens.PASSWORD_CERT = environment.getProperty("certificate.password");
		try {
			String autoconnetionStr = environment.getProperty("xmpp.autoconnection");
			if(autoconnetionStr!=null) {
				Boolean autoconnectionBool = Boolean.valueOf(autoconnetionStr);
				if(autoconnectionBool) {
					ConnectionStatus status = xmppService.connect();
					String statusStr = status.toString();
					log.warning(statusStr);
				}
			}
		}catch (Exception e) {
			log.severe("An error ocurred autoconecting! disable the flag --xmpp.autoconnection and check the configuration");
		}
		try {
			String connectionTimeOut = environment.getProperty("cim.timeout.request");
			setConnectionTimeout(connectionTimeOut);
		}catch (Exception e) {
			log.severe("Error occured parsing time specified foor connection timeout, please provide an integer value in milliseconds");
		}
		try {
			String socketTimeOut = environment.getProperty("cim.timeout.socket");
			setSoketTimeout(socketTimeOut);
		}catch (Exception e) {
			log.severe("Error occured parsing time specified foor socket timeout, please provide an integer value in milliseconds");
		}
		try {
			String xmppTimeOut = environment.getProperty("cim.timeout.xmpp");
			setXmppTimeout(xmppTimeOut);
		}catch (Exception e) {
			log.severe("Error occured parsing time specified foor xmpp timeout, please provide an integer value in milliseconds");
		}
	}
	
	private void setXmppTimeout(String xmppTimeOut) {
		if(xmppTimeOut!=null) {
			Long xmppTimeoutParsed = Long.valueOf(xmppTimeOut);
			log.warning("Xmpp timeout changed to: "+xmppTimeoutParsed);
			ConfigTokens.XMPP_TIMEOUT = xmppTimeoutParsed;
		}
		
	}


	private void setSoketTimeout(String socketTimeOut) {
		if(socketTimeOut!=null) {
			Long socketTimeoutParsed = Long.valueOf(socketTimeOut);
			log.warning("Socket timeout changed to: "+socketTimeoutParsed);
			ConfigTokens.SOKET_TIMEOUT = socketTimeoutParsed;
		}
		
	}


	private void setConnectionTimeout(String connectionTimeout) {
		if(connectionTimeout!=null) {
			Long connectionTimeoutParsed = Long.valueOf(connectionTimeout);
			log.warning("Connection timeout changed to: "+connectionTimeout);
			ConfigTokens.CONNECTION_TIMEOUT = connectionTimeoutParsed;
		}
	}
	
	@PostConstruct
	public void initUsers() {
		userService.createDefaultUser();
		validationService.readValidationShapesFile();
	}
	
	@PreDestroy
	public void disconnectPeer() {
		xmppService.disconnect();
	}


}
