package cim.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cim.ConfigTokens;
import helio.components.connector.InMemoryConnector;
import helio.components.engine.EngineImp;
import helio.framework.Connector;
import helio.framework.MappingTranslator;
import helio.framework.mapping.Mapping;
import helio.framework.objects.RDF;
import helio.mappings.translators.AutomaticTranslator;
import helio.writer.HelioWriter;
import helio.writer.framework.DevirtualisationMapping;
import helio.writer.framework.serialiser.JsonTranslator;

@Service
public class VirtualisationService {

	private Logger log = Logger.getLogger(VirtualisationService.class.getName());

	
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
				MappingTranslator translator = new AutomaticTranslator();
				Mapping mapping = translator.translate(readingMapping);
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
}
