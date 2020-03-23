package cim.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cim.model.LocalUserAuthority;
import cim.repository.LocalUserRepository;

@Service
public class UserDetailsServiceCIM implements UserDetailsService {

    @Autowired
    public LocalUserRepository userRepository;
	
    @Override
     public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
    		cim.model.User appUser = userRepository.findByUsername(username);
    		System.out.println("User found: "+appUser);
		if(appUser==null) {
			new UsernameNotFoundException("User does not exists");
		}
		List<GrantedAuthority> grantList = new ArrayList<>();
		if(appUser!=null) {
			for (LocalUserAuthority authority: appUser.getAuthority()) {
				GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getAuthority());
			    grantList.add(grantedAuthority);
		    }
		}
	    UserDetails user = new User(appUser.getUsername(), appUser.getPassword(), grantList);
	    return user;
    }
}
