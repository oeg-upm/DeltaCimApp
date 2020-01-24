package cim.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.model.Route;
import cim.repository.RouteRepository;

@Service
public class BridgingService {

	@Autowired
	public RouteRepository routeRepository;
	
	
	private static Set<Route> routes;
	
	static {
		routes = new HashSet<>();
	}
	
	@PostConstruct
	public void readAllACL() {
		routes.addAll(routeRepository.findAll());
	}
	
	public static Set<Route> getRoutes(){
		return routes;
	}
	
	@Autowired
	public KGService kgService;
	
	public List<Route> getAllRoutes(){
		return routeRepository.findAll();
	}
	
	public void update(Route route){
		Route newRoute = routeRepository.save(route);
		kgService.updateMappings();
		routes.add(newRoute);
	}

	public Boolean remove(String routeId) {
		Boolean removed = false;
		Route route = routeRepository.findByRegexPath(routeId);
		if(route!=null) {
			routeRepository.delete(route);
			removed = true;
			routes.remove(route);
		}
		kgService.stopService();
		kgService.initEngine();
		kgService.startService();
		kgService.updateMappings();
		return removed;
	}

}
