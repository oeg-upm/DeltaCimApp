package cim.xmpp.factory;

import java.util.HashSet;
import java.util.Set;

import cim.model.LocalUserAuthority;

public class AuthorityFactory {
	
	public static Set<LocalUserAuthority> createDefaultAuthority() {
		LocalUserAuthority autorityRoot = new LocalUserAuthority();
		autorityRoot.setAuthority("ROLE_ADMIN");
		autorityRoot.setId((long) 1);
		LocalUserAuthority autority = new LocalUserAuthority();
		autority.setAuthority("ROLE_USER");
		autority.setId((long) 2);

		Set<LocalUserAuthority> auth = new HashSet<>();
		auth.add(autorityRoot);
		auth.add(autority);
		autorityRoot.setId((long) 1);
		autority.setId((long) 2);

		
		return auth;
	}
	
	public static Set<LocalUserAuthority> createAdminAuthority() {
		LocalUserAuthority autorityRoot = new LocalUserAuthority();
		autorityRoot.setId((long) 1);
		autorityRoot.setAuthority("ROLE_ADMIN");
		LocalUserAuthority autority = new LocalUserAuthority();
		autority.setId((long) 2);
		autority.setAuthority("ROLE_USER");
		
		Set<LocalUserAuthority> auth = new HashSet<>();
		auth.add(autorityRoot);
		auth.add(autority);
		
		return auth;
	}
	
	public static Set<LocalUserAuthority> createNewUserAuthority() {
		LocalUserAuthority autority = new LocalUserAuthority();
		autority.setId((long) 2);
		autority.setAuthority("ROLE_USER");
		Set<LocalUserAuthority> auth = new HashSet<>();
		auth.add(autority);
		return auth;
	}
	
}
