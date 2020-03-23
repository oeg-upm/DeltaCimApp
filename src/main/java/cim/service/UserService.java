package cim.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cim.model.LocalUserAuthority;
import cim.model.User;
import cim.repository.LocalUserAuthorityRepository;
import cim.repository.LocalUserRepository;
import cim.xmpp.factory.AuthorityFactory;
import cim.xmpp.factory.UserFactory;

@Service
public class UserService {


	@Autowired
	public LocalUserRepository userRepository;

	@Autowired
	public LocalUserAuthorityRepository authorityRepository;


	public List<User> getAllUsers(){
		return userRepository.findAll();
	}

	public void createDefaultUser() {
		if(userRepository.findAll().isEmpty()) {
			User admin = UserFactory.createDefaultUser();
			Set<LocalUserAuthority> authorities = AuthorityFactory.createDefaultAuthority();
			authorities.forEach(authority -> authorityRepository.save(authority));
			admin.setAuthority(authorities);
			userRepository.save(admin);
		}
	}
	
	public void createAdmin() {
		if(userRepository.findAll().isEmpty()) {
			User admin = UserFactory.createDefaultUser();
			Set<LocalUserAuthority> authorities = AuthorityFactory.createAdminAuthority();
			authorities.forEach(authority -> authorityRepository.save(authority));
			admin.setAuthority(authorities);
			userRepository.save(admin);
		}
	}


	public void createUser(User newUser) {
		if(!userRepository.existsById(newUser.getUsername())) {
			newUser.setPassword(encode(newUser.getPassword()));
			Set<LocalUserAuthority> authorities;
			if(newUser.getAuthorityTemp().contentEquals("ROLE_ADMIN")) {
				authorities = AuthorityFactory.createAdminAuthority();
				authorities.forEach(authority -> authorityRepository.save(authority));
				newUser.setAuthority(authorities);
				userRepository.save(newUser);
			}else{
				authorities = AuthorityFactory.createNewUserAuthority();
				authorities.forEach(authority -> authorityRepository.save(authority));
				newUser.setAuthority(authorities);
				userRepository.save(newUser);
			}
		}
	}



	public Boolean remove(String userId) {
		Boolean removed = false;
		User user = userRepository.findByUsername(userId);
		if(user!=null && !user.getUsername().contentEquals("root")) {
			userRepository.delete(user);
			removed = true;
		}
		return removed;
	}

	public static String encode(String password){
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
		return bCryptPasswordEncoder.encode(password);
	}

	public Set<LocalUserAuthority> stringToSet(String authority) {
		LocalUserAuthority autorityInput = new LocalUserAuthority();
		autorityInput.setAuthority(authority);
		Set<LocalUserAuthority> auth = new HashSet<>();
		auth.add(autorityInput);
		return auth; 
	}

}
