package cim.repository.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cim.repository.model.Route;
import cim.repository.repository.RouteRepository;

@Service
public class BridgingService {

	@Autowired
	public RouteRepository routeRepository;
	
	@Autowired
	public KGService kgService;
	
	public List<Route> getAllRoutes(){
		return routeRepository.findAll();
	}
	
	public void update(Route route){
		routeRepository.save(route);
		kgService.updateMappings();
	}

	public Boolean remove(String routeId) {
		Boolean removed = false;
		Route route = routeRepository.findByRegexPath(routeId);
		if(route!=null) {
			routeRepository.delete(route);
			removed = true;
		}
		kgService.stopService();
		kgService.initEngine();
		kgService.startService();
		kgService.updateMappings();
		return removed;
	}

}
