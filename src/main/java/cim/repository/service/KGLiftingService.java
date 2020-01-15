package cim.repository.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

import cim.repository.model.Route;
import helio.components.connector.GetConnector;
import helio.components.datasource.RDFDatasource;
import helio.components.engine.EngineImp;
import helio.framework.Connector;
import helio.framework.Datasource;
import helio.framework.mapping.DatasourceMapping;
import helio.framework.mapping.Mapping;
import helio.framework.mapping.PropertyRule;
import helio.framework.mapping.ResourceRule;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;

/**
 * 
 * @author cimmino
 *
 */
@Service
public class KGLiftingService {

	
	private static EngineImp helio;
	private static Mapping mapping;
	
	static {
		mapping = initPlainMapping();
		
	}
	
	private static Mapping initPlainMapping() {
		Mapping plainMapping = new Mapping();
		ResourceRule rules = new ResourceRule();
		rules.setResourceRuleId("CIM Rules");
		rules.setProperties(new ArrayList<PropertyRule>());
		plainMapping.getResourceRules().add(rules);
		return plainMapping;
	}
	
	public void initEngine() {
		helio = new EngineImp(mapping);
	}
	
	public void startService() {
		helio.initialize();
	}
	
	public void stopService() {
		helio.close();
	}
	
	public RDF getRDF() {
		return helio.publishRDF();
	}
	
	public String solveLocalQuery(String sparqlQuery, SparqlResultsFormat format) {
		// TODO: materialise RDF, pass the shapes, and then, if correct, solve query. Otherwhise return the report
		
		return helio.query(sparqlQuery, format);
	}
	
	
	
	public void updateMappings(List<Route> routes) {
		mapping = initPlainMapping();
		
		if(routes!=null)
			routes.stream().forEach(route -> updateMappingsWithEndpoint(route.getEndpoint()));
	
		helio.setMapping(mapping);
		
	}
	
	private void updateMappingsWithEndpoint(String endpoint) {
		List<String> conenctorArguments = new ArrayList<>();
		conenctorArguments.add(endpoint);
		Connector urlConnector = new GetConnector(conenctorArguments);
		List<String> datasourceArguments = new ArrayList<>();
		datasourceArguments.add("JSON-LD");
		Datasource rdfDatasource = new RDFDatasource(urlConnector, datasourceArguments);
		
		DatasourceMapping datasourceMapping = new DatasourceMapping();
		datasourceMapping.setDatasource(rdfDatasource);
		String datasourceId = "Datasource "+endpoint;
		datasourceMapping.setId(datasourceId);
		
		mapping.getDatasources().add(datasourceMapping);
		mapping.getResourceRules().forEach(resourceRules -> resourceRules.getDatasourcesId().add(datasourceId));
		
		
	}
}
