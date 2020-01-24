package cim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.XmppUser;

public interface XmppUserRepository extends JpaRepository<XmppUser, String>{


}
