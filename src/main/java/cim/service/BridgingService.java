package cim.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import cim.model.BridgingRule;
import cim.repository.BridgingRuleRepository;

@Service
public class BridgingService {

	@Autowired
	public BridgingRuleRepository routeRepository;
		
	
	public List<BridgingRule> getAllRoutes(){
		return routeRepository.findAll();
	}
	
	public void update(BridgingRule route){
		routeRepository.save(route);
	}
	
	public BridgingRule findByEndpoint(String endpoint){
		return routeRepository.findByEndpoint(endpoint);
	}
	
	public BridgingRule findByXmppPattern(String xmppPattern){
		return routeRepository.findByXmppPattern(xmppPattern);
	}


	public Optional<BridgingRule> findByXmppPatternMatch(String xmppRoute, String method){
		return routeRepository.findAll().stream().filter(rule -> match(rule.getXmppPattern(), xmppRoute) && method.equalsIgnoreCase(rule.getMethod().toString())).findFirst();
	}
	
	private boolean match(String pattern,String value) {
		Boolean match = false; 
		Pattern reegexPattern = Pattern.compile(pattern);
	    Matcher matcher = reegexPattern.matcher(value);
	    if (matcher.find()) 
	    		match = true;
		return match;
	}
	
	public void remove(long routeId) {
		Optional<BridgingRule> route = routeRepository.findById(routeId);
		if(route.isPresent())
			routeRepository.delete(route.get());
	}
	
}
