package cim.service;

import java.util.logging.Logger;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.ConfigTokens;
import cim.model.BridgingRule;
import cim.model.P2PMessage;
import cim.model.ValidationReport;
import cim.xmpp.factory.RequestsFactory;
import cim.xmpp.factory.ValidationReportFactory;
import helio.components.engine.EngineImp;
import helio.components.engine.sparql.SparqlEndpoint;
import helio.framework.MappingTranslator;
import helio.framework.exceptions.MalformedMappingException;
import helio.framework.mapping.Mapping;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;
import helio.framework.objects.Tuple;
import helio.mappings.translators.AutomaticTranslator;


public class KGService {

	private static Logger log = Logger.getLogger(KGService.class.getName());


	// -- Query Solving methods

	
	/**
	 * This method solves a SPARQL query relying on the Semantic-Engine framework
	 * @param query A SPARQL query
	 * @param answerFormat A {@link SparqlResultsFormat} object specifying the output format
	 * @return The query results
	 */
	public static Tuple<String,Integer> solveQuery(String query, SparqlResultsFormat answerFormat, P2PMessage message, String headers){
		Tuple<String,Integer> response = new Tuple<>();
		if(!isReadableQuery(query) && isQueryCorrect(query)){
			/*Boolean correctProcess = true;
			try {
				downliftService.solveLocalQuery(liftingService.getRDF(), query);
			}catch(IllegalArgumentException e ) {
				correctProcess=false;
			}
			String responseAnswer = "{ \n" + 
					"  \"head\" : { } ,\n" + 
					"  \"boolean\" : "+correctProcess+"\n" + 
					"}";
			response.setFirstElement(responseAnswer);
			response.setSecondElement(null);*/
		}else if(isReadableQuery(query) && isQueryCorrect(query)) {
			//response = liftingService.solveLocalQuery(query, answerFormat);
			response = solveMaterialisationQuery(query, answerFormat, message, headers);
			
		}else {
			log.severe("Provided SPARQL query contains syntax errors");
			log.severe(query);
		}
		return response ;
	}
	

	
	/**
	 * This method checks syntax errors for a given SPARQL query
	 * @param query A SPARQL query
	 * @return A {@link Boolean} value specifying if the input query was correct (true), or had some errors (false)
	 */
	private static Boolean isQueryCorrect(String query) {
		Boolean isCorrect = false;
		try {
			QueryFactory.create(query);
			isCorrect = true;
		}catch (Exception e) {
			log.severe(e.getMessage());
		}
		
		return isCorrect;
	}
	
	/**
	 * This method checks syntax errors for a given SPARQL query
	 * @param query A SPARQL query
	 * @return A {@link Boolean} value specifying if the input query was correct (true), or had some errors (false)
	 */
	private static Boolean isReadableQuery(String sparqlQuery) {
		Boolean isReadQuery = false;
		try {
			Query query = QueryFactory.create(sparqlQuery);
			isReadQuery = query.isAskType() || query.isSelectType() || query.isConstructType() || query.isDescribeType();
		}catch (Exception e) {
			log.severe(e.getMessage());
		}
		
		return isReadQuery;
	}
	
	/*
	 * Materialisation methods
	 */
 
	private static Tuple<String, Integer> solveMaterialisationQuery(String query, SparqlResultsFormat answerFormat, P2PMessage message, String headers) {
		Tuple<String,Integer> result = new Tuple<>();
		try {
			// Generate virtual RDF
			Tuple<RDF,Integer> aggregatedData = aggregateLocalEndpointsRDF(message, headers);
			result.setSecondElement(aggregatedData.getSecondElement());
			// Solve query
			SparqlEndpoint querySolver = new SparqlEndpoint();
			String queryAnswer = querySolver.solveQuery(aggregatedData.getFirstElement(), query, answerFormat);
			result.setFirstElement(queryAnswer);
		} catch (Exception e) {
			log.severe(e.toString());
		}

		return result;
	}
	
	
	/**
	 * This method aggregates all the virtual RDF of all the resources registered in the briding service
	 * @param message a P2P message that is related to the request of aggregating all the virtual RDF, it can be null 
	 * @param headers a set of headers sent with the virtualization request
	 * @return A tuple in which the first argument is the aggregated RDF and the second is a code that can be 200 (everything ok) or 418 (validation error)
	 */
	private static Tuple<RDF,Integer> aggregateLocalEndpointsRDF(P2PMessage message, String headers) {
		RDF dataAggregation = new RDF();
		Integer code = null;
		Boolean error = false;
		for(BridgingRule route: BridgingService.getRoutes()) {
			String localEndpoint = route.getEndpoint();
			if(message!=null)
				localEndpoint = RequestsFactory.buildRealLocalEndpoint(message, route);
			RDF auxiliarData = virtualiseRDF(localEndpoint, route.getReadingMapping(), headers); 
			if(auxiliarData!=null) {
				code = validateRDF(auxiliarData.toString(ConfigTokens.DEFAULT_RDF_SERIALISATION), localEndpoint);
				dataAggregation.addRDF(auxiliarData);
			}
			if(code != 200) 
				error = true;
		}
		if(error)
			code = 418;
		return new Tuple<RDF,Integer>(dataAggregation, code);
	}
	
	

	
	/**
	 * This method outputs the virtual RDF that is the result of applying the provided mapping to the endpoint.<p>
	 * The method instantiates the mapping with the endpoint and the headers provided, before producing the virtual RDF.<p>
	 * Consider that if the mapping does not contain the keywords GetConnectorReplacement or GetConnectorHeadersReplacement this method will apply the mapping directly ignoring the arguments passed endpoint and headers
	 * @param endpoint a data endpoint
	 * @param rawMapping a json {@Mapping} serialization for Helio
	 * @param headers a set of headers
	 * @return the virtual RDF produced by translating the data from the endpoint
	 */
	public static RDF virtualiseRDF(String endpoint, String rawMapping, String headers) {
		RDF rdfData = null; // it is the RDF representation of heterogeneous data
		
		try {
			// The modification of the mappings must be done here because the DataFetcher injects the dynamic parameters of a requested URL
			if(rawMapping!=null && rawMapping.contains("GetConnectorReplacement")) 
				rawMapping = rawMapping.replace("#GetConnectorReplacement#", endpoint.replace("\"", "\\\""));
			if(rawMapping!=null && rawMapping.contains("#GetConnectorHeadersReplacement#") && headers!=null && !headers.isEmpty())
				rawMapping = rawMapping.replace("#GetConnectorHeadersReplacement#", headers.replace("\"", "\\\""));
			
			MappingTranslator translator = new AutomaticTranslator();
			Mapping mapping = translator.translate(rawMapping);
			EngineImp virtualiser = new EngineImp(mapping);
			virtualiser.initialize();
			rdfData = virtualiser.publishRDF();	
			virtualiser.close();
			
		} catch (Exception e) {
			log.severe(e.toString());
		}

		return rdfData;
	}
	
	public static Integer validateRDF(String responseMessage, String endpoint) {
		ValidationReport report = ValidationService.generateValidationReport(responseMessage, ConfigTokens.DEFAULT_RDF_SERIALISATION, endpoint);
		Integer code = 200;
		if(!ValidationReportFactory.isSuccessfullReport(report)) {
			code = 403;
			storeReport(report);
		}
		return code;
	}

	 private static void storeReport(ValidationReport report) {
		try {
			ValidationService.reportsQueue.add(report);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	 
	
	
}
