package cim.xmpp.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cim.model.Authority;

public class AuthorityFactory {
	
	
	
	
	public static Set<Authority> createAdminAuthority() {
		Authority autorityRoot = new Authority();
		autorityRoot.setAuthority("ROLE_ADMIN");
		Authority autority = new Authority();
		autority.setAuthority("ROLE_USER");
		
		Set<Authority> auth = new HashSet<>();
		auth.add(autorityRoot);
		auth.add(autority);
		
		return auth;
	}

}
