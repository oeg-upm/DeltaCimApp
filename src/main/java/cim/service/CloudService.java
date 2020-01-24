package cim.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.ConfigTokens;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;

@Service
public class CloudService {

	
	@Autowired
	public ACLService aclService;
	
	private Logger log = Logger.getLogger(CloudService.class.getName());
	private static final String JSONLD = "JSON-LD";
	
	public CloudService() {
		//empty
	}
	
	public String federateQuery(String queryString, SparqlResultsFormat format) {
		System.out.println(queryString);
		String answer = null;
		try {
			Set<String> endpoints = aclService.getAllUsernames().stream().map(user -> transformToDELTAURLs(user)).collect(Collectors.toSet());
			for(String endpoint:endpoints) {
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$>"+endpoint);
				endpoint = endpoint.replace("<", "").replace(">", "");
				Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
				QueryEngineHTTP qeh = new QueryEngineHTTP(endpoint, query);
				ResultSet resultSet = qeh.execSelect();
				answer = formatQueryAnswer(resultSet, format);
				qeh.close();
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		return answer;
	}
	
/*	public String federateQuery(String queryString, SparqlResultsFormat format) {
		String queryFederated = rewriteQuery(queryString, aclService.getAllUsernames());
		String answer = null;
		QueryExecution qexec = null;
		try {
			Model data = ModelFactory.createDefaultModel();
			qexec = QueryExecutionFactory.create(queryFederated, data);
			ResultSet results = qexec.execSelect();
			answer = formatQueryAnswer(results, format);
		} catch (Exception e) {
			e.printStackTrace();
			log.severe(e.toString());
		}finally {
			if(qexec!=null)
				qexec.close();
		}
		
		return answer;
	}*/
	
	private String rewriteQuery(String queryString, List<String> users) {
		Set<String> endpoints = users.stream().map(user -> transformToDELTAURLs(user)).collect(Collectors.toSet());
		// TODO: perform discovery over the endpoints
		String endpointsQueryFragment =  buildQueryServiceToken(endpoints); 
		queryString = queryString.replaceFirst("\\{", "{\n\tSERVICE ?service {");
		int lastIndex = queryString.lastIndexOf("}");
		String rewrittenQuery = queryString.substring(0, lastIndex)+endpointsQueryFragment+queryString.substring(lastIndex, queryString.length());
		System.out.println(rewrittenQuery);
		return rewrittenQuery;
	}
	
	private String transformToDELTAURLs(String user) {
		StringBuilder formatedEndpoint= new StringBuilder();
		formatedEndpoint.append("<http://").append("localhost:8080").append("/delta/").append(user).append("/sparql").append(">");
		return formatedEndpoint.toString();
	}
	
	private String buildQueryServiceToken(Set<String> endpoints) {
		StringBuilder fragment = new StringBuilder();
		fragment.append("\n\t} VALUES ?service { ");
		endpoints.forEach(endpoint -> fragment.append(endpoint).append(" "));
		fragment.append("}\n");
		return fragment.toString();
	}

	/**
	 * This method returns a string containing the query results in a specific format
	 * @param results A jena {@link ResultSet} object to be transformed into a specific format
	 * @param format A jena {@link ResultsFormat} object specifying the output format 
	 * @return A {@link String} containing the query results in the specified format
	 */
	private String formatQueryAnswer(ResultSet results, SparqlResultsFormat format) {
		ByteArrayOutputStream formattedAnswerStream = new ByteArrayOutputStream();
		if(format.equals(SparqlResultsFormat.CSV)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_CSV);
		}else if(format.equals(SparqlResultsFormat.RDF_N3)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RDF_N3);
		}else if(format.equals(SparqlResultsFormat.RDF_NT) || format.equals(SparqlResultsFormat.N_TRIPLES) || format.equals(SparqlResultsFormat.N_TRIPLE)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RDF_NT);
		}else if(format.equals(SparqlResultsFormat.RDF_TTL)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RDF_TTL);
		}else if(format.equals(SparqlResultsFormat.RDF_TURTLE)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RDF_TURTLE);
		}else if(format.equals(SparqlResultsFormat.RDF_XML)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RDF_XML);
		}else if(format.equals(SparqlResultsFormat.BIO)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_BIO);
		}else if(format.equals(SparqlResultsFormat.COUNT)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_COUNT);
		}else if(format.equals(SparqlResultsFormat.JSON)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_JSON);
		}else if(format.equals(SparqlResultsFormat.SSE)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_SSE);
		}else if(format.equals(SparqlResultsFormat.THRIFT)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_THRIFT);
		}else if(format.equals(SparqlResultsFormat.TSV)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_TSV);
		}else if(format.equals(SparqlResultsFormat.XML)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_XML);
		}else if(format.equals(SparqlResultsFormat.TEXT)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_TEXT);
		}else if(format.equals(SparqlResultsFormat.TRIG)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_TRIG);
		}else if(format.equals(SparqlResultsFormat.TUPLES)) {
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_TUPLES);
		}else if(format.equals(SparqlResultsFormat.JSON_LD)){
			 ByteArrayOutputStream formattedAnswerStreamAux = new ByteArrayOutputStream();
			 ResultSetFormatter.output(formattedAnswerStreamAux, results, ResultsFormat.FMT_RDF_TURTLE);
			 RDF rdfData = new RDF();
			 rdfData.parseRDF(new String(formattedAnswerStreamAux.toByteArray()));
			 try {
				 formattedAnswerStream.write(rdfData.toString(JSONLD).getBytes());
			} catch (IOException e) {
				log.severe("An error happened when transforming SPARQL answer into JSON-LD");
			}
		}else if(format.equals(SparqlResultsFormat.HTML)){
			try {
				String answerHTMLAux = SparqlResultsFormat.fromRSToHTML(results);
				formattedAnswerStream.write(answerHTMLAux.getBytes());
			} catch (Exception e) {
				log.severe("An error happened when transforming SPARQL answer into HTML");
			}
		}else {	
			String message = ("Provided format for SELECT query answer is not supported: ").concat(format.toString());
			log.severe(message);
			log.warning("Returning answer in JSON format");
			ResultSetFormatter.output(formattedAnswerStream, results, ResultsFormat.FMT_RS_JSON);
		}
			
		return new String(formattedAnswerStream.toByteArray());
	}
	
	
	
}
