package cim;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // This prevents Spring to create default error-handling route /error  (exclude = ErrorMvcAutoConfiguration.class )
@EnableAsync
@EnableScheduling
public class DeltaCimApplication {

	@Autowired
	public Environment environment;
	
	
	public static void main(String[] args) {
		SpringApplication.run(DeltaCimApplication.class, args);
	}
	
	
	@PostConstruct
	public void initLocalPort() {
		ConfigTokens.LOCAL_PORT = environment.getProperty("local.server.port");
	}

}
