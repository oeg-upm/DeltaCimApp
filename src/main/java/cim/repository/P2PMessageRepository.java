package cim.repository;

import org.springframework.data.repository.CrudRepository;

import cim.model.P2PMessage;

public interface P2PMessageRepository extends CrudRepository<P2PMessage, String> { 
	
	
}
