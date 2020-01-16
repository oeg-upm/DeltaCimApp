package cim.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cim.model.Acl;

public interface AclRepository  extends JpaRepository<Acl, String> {

	public Acl findByUsername(String domain);
	@Query("SELECT DISTINCT user.username FROM Acl AS user")
	public List<String> getAllUsernames();
}

