package cim.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

import cim.model.Acl;
import cim.model.P2PMessage;
import cim.model.enums.Method;
import cim.repository.AclRepository;

@Service
public class ACLService {

	@Autowired
	public AclRepository aclRepository;

	private Logger log = Logger.getLogger(ACLService.class.getName());

	public List<Acl> getXmppAcls(){
		return aclRepository.findAll();
	}
	
	public void update(Acl acl){
		aclRepository.delete(acl);
		aclRepository.save(acl);
	}

	public void remove(String xmppUsername) {
		Acl acl = aclRepository.findByUsername(xmppUsername);
		aclRepository.delete(acl);
	}
	
	public List<String> getAllXmppUsernames(){
		return aclRepository.findAll().stream().map(Acl::getUsername).collect(Collectors.toList());
	}
	
	private static final String XMPP_DOMAIN_CHAR = "@";

	public Boolean isAuthorized(String p2pUser, P2PMessage message) {
		Boolean isAuthorized = false;
		String userS = p2pUser;
		if(p2pUser.contains(XMPP_DOMAIN_CHAR))
			userS = p2pUser.substring(0, p2pUser.indexOf(XMPP_DOMAIN_CHAR));
		
		Optional<Acl> userOpt = aclRepository.findById(userS);
		if(userOpt.isPresent()) {
			Acl user = userOpt.get();
			isAuthorized = !user.isReadable() || (user.isReadable() && message.getMethod().equalsIgnoreCase(Method.GET.toString()));
		}
		log.info("Checking authorisation for: "+userS+" -> "+isAuthorized);
		return isAuthorized;
	}


}

