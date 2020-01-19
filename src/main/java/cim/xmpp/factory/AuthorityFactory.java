package cim.xmpp.factory;

import java.util.HashSet;
import java.util.Set;

import cim.model.Authority;

public class AuthorityFactory {
	
	public static Set<Authority> createAdminAuthority() {
		Authority autorityRoot = new Authority();
		autorityRoot.setId((long) 1);
		autorityRoot.setAuthority("ROLE_ADMIN");
		Authority autority = new Authority();
		autority.setId((long) 2);
		autority.setAuthority("ROLE_USER");
		
		Set<Authority> auth = new HashSet<>();
		auth.add(autorityRoot);
		auth.add(autority);
		
		return auth;
	}
	
	public static Set<Authority> createNewUserAuthority() {
		Authority autority = new Authority();
		autority.setId((long) 2);
		autority.setAuthority("ROLE_USER");
		Set<Authority> auth = new HashSet<>();
		auth.add(autority);
		return auth;
	}
	
}
