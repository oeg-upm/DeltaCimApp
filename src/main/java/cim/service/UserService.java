package cim.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public void createDefaultUser() {
		if(userRepository.findAll().isEmpty()) {
			User admin = UserFactory.createDefaultUser();
			Set<Authority> authorities = AuthorityFactory.createAdminAuthority();
			authorities.forEach(authority -> authorityRepository.save(authority));
			admin.setAuthority(authorities);
			userRepository.save(admin);
		}
			}

}
