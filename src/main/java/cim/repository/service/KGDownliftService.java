package cim.repository.service;

import java.util.List;

import org.springframework.stereotype.Service;

import cim.repository.model.Route;
import helio.framework.objects.RDF;
import helio.framework.writing.mapping.LowerMapping;
import helio.writer.components.Engine;


@Service
public class KGDownliftService {

	private static Engine helio;
	private static LowerMapping mapping;
	
	static {
		mapping = new LowerMapping();
		// TODO init mapping
	}
	
	public void initEngine() {
		helio = new Engine(null);
	}
	
	public void startService() {
		// empty
	}
	
	public void stopService() {
		// empty
	}
	
	public void updateMappings(List<Route> routes ) {
		// TODO
	}
	
	// -- Query solving methods
	public void solveLocalQuery(RDF data, String sparqlQuery) {
		helio.solveQuery(data, sparqlQuery);
	}
	
}
