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
