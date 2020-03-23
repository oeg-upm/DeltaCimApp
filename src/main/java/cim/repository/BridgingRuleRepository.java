package cim.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.BridgingRule;

public interface BridgingRuleRepository  extends JpaRepository<BridgingRule, String> {

	public BridgingRule findByRegexPath(String regexPath);

	
}
