package cim.xmpp.factory;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import cim.model.User;

public class UserFactory {

	
	private UserFactory() {
		
	}
	
	
	public static User createDefaultUser() {
		User user = new User();
		user.setUsername("root");
		user.setPassword(encode("root"));
		return user;
	}
	
	
	public static String encode(String password){
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		return bCryptPasswordEncoder.encode(password);
    }
}
