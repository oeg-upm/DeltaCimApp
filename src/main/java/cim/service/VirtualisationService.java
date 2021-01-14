package cim.service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cim.ConfigTokens;
import cim.factory.PayloadsFactory;
import cim.model.BridgingRule;
import cim.model.P2PMessage;
import cim.model.enums.Method;
import cim.service.components.RequestProcessor;
import helio.components.connector.InMemoryConnector;
import helio.components.engine.EngineImp;
import helio.components.engine.sparql.SparqlEndpoint;
import helio.framework.Connector;
import helio.framework.MappingTranslator;
import helio.framework.mapping.Mapping;
import helio.framework.objects.RDF;
import helio.framework.objects.SparqlResultsFormat;
import helio.framework.objects.Tuple;
import helio.mappings.translators.AutomaticTranslator;
import helio.writer.HelioWriter;
import helio.writer.framework.DevirtualisationMapping;
import helio.writer.framework.serialiser.JsonTranslator;

@Service
public class VirtualisationService {

	private Logger log = Logger.getLogger(VirtualisationService.class.getName());

	@Autowired
	private BridgingService bridgingService;
	@Autowired
	private RequestProcessor requestProcessor;
	private static Map<String,SparqlResultsFormat> sparqlResponseFormats;	
	
	// -- Translate to RDF
	
	public RDF normalisePayload(String payload, String xmppRemotePath, String method) {
		RDF normalisedData =  null;
		try {
			Model model = parseFromString(payload);
			normalisedData = new RDF();
			normalisedData.getRDF().add(model);
		} catch(Exception e) {
			e.printStackTrace();
			log.warning("Provided payload is not expressed in JSON-LD, looking for interoperability module to adapt data");
			Optional<BridgingRule> ruleOptional = bridgingService.findByXmppPatternMatch(xmppRemotePath, method);
			if(ruleOptional.isPresent()) {
				BridgingRule rule = ruleOptional.get();
				rule.updateMappingContent(); // update mappings
				String mapping = rule.getReadingMapping();
				if(mapping!=null) {
					normalisedData = virtualiseData(payload, mapping);
				}else {
					log.severe("Bridging rule found, but has no reading mapping associated");
					log.severe(rule.toString());
				}
			}else {
				log.warning("No interoperability module found for adapting the data");
			}
		}
		return normalisedData;
	}
	
	private Model parseFromString(String modelString) throws Exception{
		Model model = ModelFactory.createDefaultModel();
		if(modelString!=null && !modelString.isEmpty()) {
			java.io.InputStream is = IOUtils.toInputStream(modelString, StandardCharsets.UTF_8.toString());
			model.read(is, ConfigTokens.DEFAULT_URI_BASE, ConfigTokens.DEFAULT_RDF_SERIALISATION);
			is.close();
		}
		return model;
	}
	
	public RDF virtualiseData(String data, String readingMapping) {
		RDF rdfData = null;
		try {
			// The modification of the mappings must be done here because the DataFetcher injects the dynamic parameters of a requested URL
			if(readingMapping!=null && readingMapping.contains("#GetConnectorReplacementId#")) {
				readingMapping = readingMapping.replace("#GetConnectorReplacementId#", String.valueOf(data.hashCode())).replace(", \"#GetConnectorHeadersReplacement#\"", "");
				// update raw mapping
				List<String> connectorArguments = new ArrayList<>();
				connectorArguments.add(data);
				Connector connector = new InMemoryConnector(connectorArguments); 
				// 
				MappingTranslator translator = new helio.mappings.translators.JsonTranslator();
				System.out.println(translator.isCompatible(readingMapping));
				Mapping mapping = translator.translate(readingMapping);
				System.out.println(">>><<<<");
				mapping.getDatasources().forEach(ds -> ds.getDatasource().setConnector(connector));
				EngineImp virtualiser = new EngineImp(mapping);
				virtualiser.initialize();
				rdfData = virtualiser.publishRDF();	
				virtualiser.close();
			}else {
				log.severe("VirtualisationService:virtualiseData provided interoperability modukle is not compatible, check mandatory keyword #GetConnectorReplacementId#");
			}
		}catch (Exception e) {
			log.severe("VirtualisationService:virtualiseData an error ocurred");
			log.severe(e.toString());
		} catch (java.lang.NoSuchMethodError e) {
			e.printStackTrace();
			log.severe("VirtualisationService:virtualiseData payload incompatible with interoperability module");
			log.severe(e.toString());
		}
		return rdfData;
	}
	

	public RDF virtualiseLocalGet(String endpoint, String rawMapping, String headers) {
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
			log.severe("VirtualisationService:virtualiseLocalGet an error ocurred");
			log.severe(e.toString());
		}

		return rdfData;
	}
	
	
	// -- Translate from RDF
	
	public String translatePayload(String payload, String xmppRemotePath) {
		String devirtualisedData = "";
		Optional<BridgingRule> ruleOptional = bridgingService.findByXmppPatternMatch(xmppRemotePath, Method.POST.toString());
		if (ruleOptional.isPresent()) {
			BridgingRule rule = ruleOptional.get();
			rule.updateMappingContent(); // update mappings
			String mapping = rule.getWrittingMapping();
			if (mapping != null) {
				// need devirtualisation
				devirtualisedData = devirtualizeData(payload, mapping);
			} else {
				// no need of anything else if data is already json-ld
				if(isJsonLDdata(payload)){
					devirtualisedData = payload;
				}else {
					log.severe("Remote request linekd to a Bridging rule, but it has no writting mapping associated");
					String ruleString = rule.toString();
					log.severe(ruleString);
				}
			}
		} else {
			log.warning("No interoperability module found for adapting the data");
		}
		return devirtualisedData;
	}
	
	public Boolean isJsonLDdata(String data) {
		RDF normalisedData = null;
		try {
			Model model = ModelFactory.createDefaultModel();
			model.read(IOUtils.toInputStream(data, StandardCharsets.UTF_8.toString()), null, ConfigTokens.DEFAULT_RDF_SERIALISATION);
			normalisedData = new RDF();
			normalisedData.getRDF().add(model);
		}catch(Exception e) {
			log.severe("Data is not json-ld");
		}
		return normalisedData!=null;
	}
	
	public String devirtualizeData(String data, String writtingMapping) {
		String transaltedData = null;
		if(writtingMapping.startsWith("# RDF")) {
			String format = writtingMapping.split(":")[1].trim();
			try {
				RDF rdfData = new RDF();
				rdfData.parseRDF(data, ConfigTokens.DEFAULT_RDF_SERIALISATION);
				transaltedData = rdfData.toString(format);
			}catch (Exception e) {
				log.severe("VirtualisationService:devirtualizeData error parsing RDF data");
			}
		}else {
			// DONE: run devirtualisation engine 
			try {
				writtingMapping = writtingMapping.replaceAll("#GetConnectorReplacement#", data.replace("\"", "\\\\\""));
				JsonObject mappingJson = new Gson().fromJson(writtingMapping, JsonObject.class);
				JsonTranslator translator = new JsonTranslator();
				DevirtualisationMapping mapping = translator.translate(mappingJson.toString());
				HelioWriter writer = new HelioWriter(mapping);
				transaltedData = writer.devirtualise();
			}catch(Exception e) {
				log.severe("VirtualisationService:devirtualizeData error translating from RDF data");
			}
		}
		return transaltedData;
	}

	
	// -- Answer queries
	public Tuple<String,Integer> solveQuery(P2PMessage message) {
		// Extract query
		String sparqlQuery = message.getMessage();
		if(message.getMethod().equalsIgnoreCase(Method.GET.toString())) {
			sparqlQuery = retrieveSPARQLQuery(message.getRequest());
		}
		return solveQuery(sparqlQuery, message.getHeaders());
	}
	
	public Tuple<String,Integer> solveQuery(String query, String headers) {
		Map<String,String> headersMap = requestProcessor.retrieveHeaders(headers);
		return  solveQuery(query, headersMap);
	}
	
	
	public Tuple<String,Integer> solveQuery(String query, Map<String,String> headersMap) {
		Tuple<String,Integer> tuple = checkQuerySyntax(query);
		if(tuple==null) {
			// Gather all the possible RDF form endpoints (translating when required from other formats)
			RDF dataGathered = new RDF();
			Map<String,String> emptyHeaders = new HashMap<>();
			List<Tuple<String,Integer>> responsesGathered = bridgingService.getAllRoutes().parallelStream().filter(rule -> rule.getMethod().equals(Method.GET)).map(rule -> requestProcessor.solveGetRequest(rule.getEndpoint(), emptyHeaders)).collect(Collectors.toList());
			responsesGathered.parallelStream().forEach(responseGathered -> dataGathered.addRDF(buildRDF(responseGathered.getFirstElement())));
			
			 // Check if one endpoint did not responded correctly
			Boolean partialResponse = responsesGathered.parallelStream().anyMatch(responseGathered -> responseGathered.getSecondElement()> 299);
			// get query result
			SparqlResultsFormat queryResultsFormat = extractResponseAnswerFormat(headersMap);
			String queryResponse = answerQuery(dataGathered, query, queryResultsFormat); 
			// build reponse tuple
			tuple = new Tuple<>(queryResponse,200);
			if(partialResponse)
				tuple.setSecondElement(206); // partial content
		}
		return tuple;
	}
	
	private String answerQuery(RDF dataGathered, String query, SparqlResultsFormat queryResultsFormat) {
		SparqlEndpoint querySolver = new SparqlEndpoint();
		return querySolver.solveQuery(dataGathered, query, queryResultsFormat);
	}

	private Tuple<String, Integer> checkQuerySyntax(String query) {
		Tuple<String, Integer> response = null;
		Query providedQuery = null;
		try {
			providedQuery = QueryFactory.create(query);
		}catch(Exception e) {
			log.severe("Provided SPARQL query has syntax errors");
		}finally {
			if(providedQuery==null) {
				response = PayloadsFactory.getErrorPayloadSyntaxQueryErrors();
			}else if(!providedQuery.isAskType() && !providedQuery.isSelectType() && !providedQuery.isDescribeType() && !providedQuery.isConstructType()) {
				response = PayloadsFactory.getErrorPayloadQueryNotSupported();
			}
		}
		return response;
	}

	private RDF buildRDF(String data) {
		RDF rdf = new RDF();
		rdf.parseRDF(data, ConfigTokens.DEFAULT_RDF_SERIALISATION);
		return rdf;
	}
	
	private static final String EQUAL_TOKEN = "=";
	private String retrieveSPARQLQuery(String endpoint) {
		String sparqlQuery = null;
		try {
			if(!endpoint.startsWith("http"))
				endpoint = "http://localhost"+endpoint;
			URL url = new URL(endpoint);
			String query = url.getQuery();
			String[] pairs = query.split("&");
			for (String pair : pairs) {
				int idx = pair.indexOf(EQUAL_TOKEN);
				String keyword = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8.toString());
				if (keyword.equals("query") || keyword.equals("update")) {
					sparqlQuery = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8.toString());
					break;
				}
			}
		} catch (Exception e) {
			log.severe(e.toString());
		}
		return sparqlQuery;
	}
	
	public String cleanQuery(String query) {
		String cleanedQuery = query;
		try {
			// When query comes from the get it has the query=...
			if (cleanedQuery.startsWith("query="))
				cleanedQuery = cleanedQuery.substring(6);
			if (cleanedQuery.startsWith("update="))
				cleanedQuery = cleanedQuery.substring(7);
			cleanedQuery = java.net.URLDecoder.decode(cleanedQuery, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			log.severe(e.toString());
		}
		return cleanedQuery;
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
