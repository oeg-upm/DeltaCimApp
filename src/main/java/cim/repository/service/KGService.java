package cim.repository.service;



import java.util.List;
import java.util.logging.Logger;
import javax.inject.Singleton;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cim.repository.model.Route;
import helio.framework.objects.SparqlResultsFormat;


@Service
public class KGService {

	@Autowired
	public BridgingService routeService;
	
	@Autowired
	@Singleton
	public KGLiftingService liftingService;
	
	@Autowired
	@Singleton
	public KGDownliftService downliftService;
	
	private Logger log = Logger.getLogger(KGService.class.getName());

	
	// Managing methods
	
	public void initEngine() {
		liftingService.initEngine();
		downliftService.initEngine();
	}
	
	public void startService() {
		liftingService.startService();
		downliftService.startService();
	}
	
	public void stopService() {
		liftingService.stopService();
		downliftService.stopService();
	}
	
	public void updateMappings() {
		System.out.println("UPDATING MAPPINGS");
		List<Route> routes = routeService.getAllRoutes();
		liftingService.updateMappings(routes);
		downliftService.updateMappings(routes);
	}
	
	
	// -- Query Solving methods

	/**
	 * This method solves a SPARQL query relying on the Semantic-Engine framework
	 * @param query A SPARQL query
	 * @param answerFormat A {@link SparqlResultsFormat} object specifying the output format
	 * @return The query results
	 */
	public String solveQuery(String query, SparqlResultsFormat answerFormat){
		String response = null;
		if(!isReadableQuery(query) ){
			Boolean correctProcess = true;
			try {
				downliftService.solveLocalQuery(liftingService.getRDF(), query);
			}catch(IllegalArgumentException e ) {
				correctProcess=false;
			}
			response = "{ \n" + 
					"  \"head\" : { } ,\n" + 
					"  \"boolean\" : "+correctProcess+"\n" + 
					"}";
		}else if(isQueryCorrect(query)) {
			response = liftingService.solveLocalQuery(query, answerFormat);
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
	private Boolean isQueryCorrect(String query) {
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
	private Boolean isReadableQuery(String sparqlQuery) {
		Boolean isReadQuery = false;
		try {
			Query query = QueryFactory.create(sparqlQuery);
			isReadQuery = query.isAskType() || query.isSelectType() || query.isConstructType() || query.isDescribeType();
		}catch (Exception e) {
			log.severe(e.getMessage());
		}
		
		return isReadQuery;
	}
	
}
