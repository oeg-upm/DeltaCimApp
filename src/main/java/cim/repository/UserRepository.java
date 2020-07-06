package cim.repository;

import java.util.List;

import cim.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface UserRepository extends JpaRepository<User, String> {

	User findByUsername(String username);
	@Query("SELECT DISTINCT user.username FROM User AS user")
	public List<String> getAllUsernames();
	
}
