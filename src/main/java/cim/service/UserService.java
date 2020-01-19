package cim.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import cim.model.Acl;
import cim.model.Authority;
import cim.model.User;
import cim.repository.AuthorityRepository;
import cim.repository.UserRepository;
import cim.xmpp.factory.AuthorityFactory;
import cim.xmpp.factory.UserFactory;

@Service
public class UserService {


	@Autowired
	public UserRepository userRepository;

	@Autowired
	public AuthorityRepository authorityRepository;


	public List<User> getAllUsers(){
		return userRepository.findAll();
	}

	public void createDefaultUser() {
		if(userRepository.findAll().isEmpty()) {
			User admin = UserFactory.createDefaultUser();
			Set<Authority> authorities = AuthorityFactory.createAdminAuthority();
			authorities.forEach(authority -> authorityRepository.save(authority));
			admin.setAuthority(authorities);
			userRepository.save(admin);
		}
	}


	public void createUser(User newUser) {
		if(!userRepository.existsById(newUser.getUsername())) {
			newUser.setPassword(encode(newUser.getPassword()));
			Set<Authority> authorities;
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

	public Set<Authority> stringToSet(String authority) {
		Authority autorityInput = new Authority();
		autorityInput.setAuthority(authority);
		Set<Authority> auth = new HashSet<>();
		auth.add(autorityInput);
		return auth; 
	}

}
