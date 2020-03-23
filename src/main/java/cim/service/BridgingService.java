package cim.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.model.BridgingRule;
import cim.repository.BridgingRuleRepository;

@Service
public class BridgingService {

	@Autowired
	public BridgingRuleRepository routeRepository;
	
	private static Set<BridgingRule> routes;
	
	static {
		routes = new HashSet<>();
	}
	
	@PostConstruct
	public void readAllACL() {
		routes.addAll(routeRepository.findAll());
	}
	
	public static Set<BridgingRule> getRoutes(){
		return routes;
	}
	
	
	public List<BridgingRule> getAllRoutes(){
		return routeRepository.findAll();
	}
	
	public void update(BridgingRule route){
		BridgingRule newRoute = routeRepository.save(route);
		routes.add(newRoute);
	}

	public Boolean remove(String routeId) {
		Boolean removed = false;
		BridgingRule route = routeRepository.findByRegexPath(routeId);
		if(route!=null) {
			routeRepository.delete(route);
			removed = true;
			routes.remove(route);
		}
		return removed;
	}

}
