package cim.repository;

import cim.model.XmppUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface XmppUserRepository extends JpaRepository<XmppUser, String>{


}
