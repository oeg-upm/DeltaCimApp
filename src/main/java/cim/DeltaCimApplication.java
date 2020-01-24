package cim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // This prevents Spring to create default error-handling route /error  (exclude = ErrorMvcAutoConfiguration.class )
@EnableAsync
@EnableScheduling
public class DeltaCimApplication {

	public static void main(String[] args) {
		SpringApplication.run(DeltaCimApplication.class, args);
	}

}
