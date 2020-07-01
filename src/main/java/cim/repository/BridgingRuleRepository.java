package cim.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.BridgingRule;

public interface BridgingRuleRepository  extends JpaRepository<BridgingRule, Long> {

	public BridgingRule findByXmppPattern(String regexPath);
	public BridgingRule findByEndpoint(String endpoint);
	
}
