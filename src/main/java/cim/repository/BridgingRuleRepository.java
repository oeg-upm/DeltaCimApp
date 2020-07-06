package cim.repository;
import cim.model.BridgingRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BridgingRuleRepository  extends JpaRepository<BridgingRule, Long> {

	public BridgingRule findByXmppPattern(String regexPath);
	public BridgingRule findByEndpoint(String endpoint);
	
}
