package cim.repository.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cim.repository.model.Acl;

public interface AclRepository  extends JpaRepository<Acl, String> {

	public Acl findByUsername(String domain);
	
}

