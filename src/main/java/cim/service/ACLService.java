package cim.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import cim.model.Acl;
import cim.model.P2PMessage;
import cim.model.enums.Method;
import cim.repository.AclRepository;

@Service
public class ACLService {

	@Autowired
	public AclRepository aclRepository;

	
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

	public Boolean isAuthorized(String p2pUser, P2PMessage message) {
		Boolean isAuthorized = false;
		Optional<Acl> userOpt = aclRepository.findById(p2pUser);
		if(userOpt.isPresent()) {
			Acl user = userOpt.get();
			isAuthorized = !user.isReadable() || (user.isReadable() && message.getMethod().equalsIgnoreCase(Method.GET.toString()));
		}
		return isAuthorized;
	}


}

