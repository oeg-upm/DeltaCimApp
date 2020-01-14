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
	
	public List<Route> getAllRoutes(){
		return routeRepository.findAll();
	}
	
	public void update(Route route){
		routeRepository.save(route);
	}

	public void remove(Route route) {
		routeRepository.delete(route);
	}

}
