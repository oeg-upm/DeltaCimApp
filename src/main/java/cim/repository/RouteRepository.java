package cim.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import cim.model.Route;

public interface RouteRepository  extends JpaRepository<Route, String> {

	public Route findByRegexPath(String regexPath);

	
}
