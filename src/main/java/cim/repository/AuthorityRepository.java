package cim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

}
