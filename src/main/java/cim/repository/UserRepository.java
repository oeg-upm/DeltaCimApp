package cim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cim.model.User;

public interface UserRepository extends JpaRepository<User, String> {

	User findByUsername(String username);

}
