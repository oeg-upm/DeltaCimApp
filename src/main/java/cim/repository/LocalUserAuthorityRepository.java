package cim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.LocalUserAuthority;

public interface LocalUserAuthorityRepository extends JpaRepository<LocalUserAuthority, String> {

}
