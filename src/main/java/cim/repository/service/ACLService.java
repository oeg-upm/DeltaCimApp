package cim.repository.service;

import java.util.List;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.repository.model.Acl;
import cim.repository.repository.AclRepository;

@Service
public class ACLService {

	@Autowired
	public AclRepository aclRepository;

	public List<Acl> getAllACL(){
		return aclRepository.findAll();
	}

	public void update(Acl acl){
		aclRepository.save(acl);
	}

	public Boolean remove(String aclId) {
		Boolean removed = false;
		Acl acl = aclRepository.findByUsername(aclId);
		if(acl!=null) {
			aclRepository.delete(acl);
			removed = true;
		}
		return removed;
	}
	

}

