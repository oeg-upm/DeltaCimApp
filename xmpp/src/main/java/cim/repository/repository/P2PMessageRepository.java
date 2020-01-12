package cim.repository.repository;

import org.springframework.data.repository.CrudRepository;

import cim.repository.model.P2PMessage;

public interface P2PMessageRepository extends CrudRepository<P2PMessage, String> { 
	
	
}
