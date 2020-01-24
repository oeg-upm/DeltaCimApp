package cim.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.model.Acl;
import cim.repository.AclRepository;

@Service
public class ACLService {

	@Autowired
	public AclRepository aclRepository;

	private static Set<String> p2pAuthorisedUsers;
	
	static {
		p2pAuthorisedUsers = new HashSet<>();
	}
	
	@PostConstruct
	public void readAllACL() {
		p2pAuthorisedUsers.addAll(aclRepository.getAllUsernames());
	}
	
	public List<Acl> getAllACL(){
		return aclRepository.findAll();
	}
	
	public List<String> getAllUsernames(){
		return aclRepository.getAllUsernames();
	}


	
	public void update(Acl acl){
		Acl saved = aclRepository.save(acl);
		if(saved!=null)
			p2pAuthorisedUsers.add(saved.getUsername());
	}

	public Boolean remove(String aclId) {
		Boolean removed = false;
		Acl acl = aclRepository.findByUsername(aclId);
		if(acl!=null) {
			aclRepository.delete(acl);
			p2pAuthorisedUsers.remove(acl.getUsername());
			removed = true;
		}
		return removed;
	}
	


	public static Boolean isAuthorized(String p2pUser) {
		System.out.println(p2pAuthorisedUsers);
		return p2pAuthorisedUsers.contains(p2pUser);
	}


}

