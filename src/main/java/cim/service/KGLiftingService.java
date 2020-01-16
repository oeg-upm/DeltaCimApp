package cim.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.model.Route;
import cim.model.ValidationReport;
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
import helio.framework.objects.Tuple;

/**
 * 
 * @author cimmino
 *
 */
@Service
public class KGLiftingService {

	@Autowired
	public ValidationService validationReport;
	
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
	
	public Tuple<String,ValidationReport> solveLocalQuery(String sparqlQuery, SparqlResultsFormat format) {
		Tuple<String,ValidationReport> result = new  Tuple<>();
		// 0. Validate RDF materialised
		RDF rdfMaterialised = helio.publishRDF();
		ValidationReport report = validationReport.validateRDF(rdfMaterialised.toString("TURTLE"), "TURTLE");
		// 1. Solve query
		String queryResults = helio.query(sparqlQuery, format);
		// 2. Create result
		result.setFirstElement(queryResults);
		result.setSecondElement(report);
		
		return result;
	}
	
	
	
	public void updateMappings(List<Route> routes) {
		mapping = initPlainMapping();
		
		if(routes!=null)
			routes.stream().forEach(route -> updateMappingsWithEndpoint(route));
	
		helio.setMapping(mapping);
		
	}
	
	private void updateMappingsWithEndpoint(Route route) {
		List<String> conenctorArguments = new ArrayList<>();
		String endpoint = route.getEndpoint();
		conenctorArguments.add(endpoint);
		Connector urlConnector = new GetConnector(conenctorArguments);
		List<String> datasourceArguments = new ArrayList<>();
		datasourceArguments.add(route.getFormat());
		Datasource rdfDatasource = new RDFDatasource(urlConnector, datasourceArguments);
		
		DatasourceMapping datasourceMapping = new DatasourceMapping();
		datasourceMapping.setDatasource(rdfDatasource);
		String datasourceId = "Datasource "+endpoint;
		datasourceMapping.setId(datasourceId);
		
		mapping.getDatasources().add(datasourceMapping);
		mapping.getResourceRules().forEach(resourceRules -> resourceRules.getDatasourcesId().add(datasourceId));
		
		
	}
}
