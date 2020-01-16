package cim.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import helio.framework.objects.SparqlResultsFormat;

public class AbstractSparqlController extends AbstractController { 

	private static Map<String,SparqlResultsFormat> sparqlResponseFormats;	

	@Override
	protected void prepareResponse(HttpServletResponse response) {
		response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR ); // by default response code is BAD
	}	
	
	/**
	 * This method extracts from the request headers the right {@link SparqlResultsFormat} to format the query results
	 * @param headers A set of headers
	 * @return A {@link SparqlResultsFormat} object
	 */
	protected SparqlResultsFormat extractResponseAnswerFormat(Map<String, String> headers) {
		String format = "application/json";
		if(headers!=null && !headers.isEmpty()) {
			if(headers.containsKey("accept"))
				format = headers.get("accept");
			if(headers.containsKey("Accept"))
				format = headers.get("Accept");
		}
		SparqlResultsFormat specifiedFormat = sparqlResponseFormats.get(format);
		if(specifiedFormat == null)
			specifiedFormat = SparqlResultsFormat.JSON;
		return specifiedFormat;
	}
	
	
	static{
		sparqlResponseFormats = new HashMap<>();
		sparqlResponseFormats.put("application/sparql-results+xml", SparqlResultsFormat.XML );
		sparqlResponseFormats.put("text/rdf+n3", SparqlResultsFormat.RDF_N3 );
		sparqlResponseFormats.put("text/rdf+ttl", SparqlResultsFormat.RDF_TTL );
		sparqlResponseFormats.put("text/rdf+turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/n3", SparqlResultsFormat.RDF_N3 );
		sparqlResponseFormats.put("application/turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("application/x-turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("application/x-nice-turtle", SparqlResultsFormat.RDF_TURTLE );
		sparqlResponseFormats.put("text/rdf+nt", SparqlResultsFormat.RDF_NT );
		sparqlResponseFormats.put("text/plain", SparqlResultsFormat.TEXT );
		sparqlResponseFormats.put("text/ntriples", SparqlResultsFormat.N_TRIPLES );
		sparqlResponseFormats.put("application/x-trig", SparqlResultsFormat.TRIG );
		sparqlResponseFormats.put("application/rdf+xml", SparqlResultsFormat.RDF_XML );
		sparqlResponseFormats.put("text/html", SparqlResultsFormat.HTML );
		sparqlResponseFormats.put("text/csv", SparqlResultsFormat.CSV );
		sparqlResponseFormats.put("text/tab-separated-values", SparqlResultsFormat.TSV );
		sparqlResponseFormats.put("application/json", SparqlResultsFormat.JSON );
		sparqlResponseFormats.put("application/sparql-results+json", SparqlResultsFormat.JSON );
	}
}
