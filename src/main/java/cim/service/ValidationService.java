package cim.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mashape.unirest.http.Unirest;

import cim.ConfigTokens;
import cim.model.ValidationReport;
import cim.repository.ValidationReportRepository;
import cim.xmpp.factory.ValidationReportFactory;
import helio.framework.objects.RDF;

@Service
public class ValidationService {

	@Autowired
	public ValidationReportRepository validationRepository;
	private Logger log = Logger.getLogger(ValidationService.class.getName());
	public String validationShapes;
		
	public void initShapes() {
		validationShapes = readFile(ConfigTokens.VALIDATIONS_SHAPES_FILE);
	}
	
	 public String readFile(String fileName) {
		 StringBuilder data = new StringBuilder();
			// 1. Read the file
			try {
				FileReader file = new FileReader(fileName);
				BufferedReader bf = new BufferedReader(file);
				// 2. Accumulate its lines in the data var
				bf.lines().forEach( line -> data.append(line).append("\n"));
				bf.close();   
				file.close();
			}catch(Exception e) {
				log.severe(e.toString());
			} 
			return data.toString();
	 }

	 public ValidationReport validateRDFEndpoint(String endpoint, String format) {
		 ValidationReport validationReport = null;
		 try {
			 String rdfDocument = Unirest.get(endpoint).asString().getBody();
			 validationReport = validateAndStore(rdfDocument, format, endpoint);
		 }catch(Exception e) {
			log.severe(e.toString());
		}
		 return validationReport;
	 }
	 
	public ValidationReport validateRDF(String rdfDocument, String format) {
		return validateAndStore(rdfDocument, format, null);
	}
	
	private ValidationReport validateAndStore(String rdfDocument, String format, String endpoint) {
		ValidationReport validationReport = null;
		try {
			RDF rdf = new RDF();
			rdf.parseRDF(rdfDocument, format);
			RDF validationResult = rdf.validateShape(validationShapes);
			validationReport = ValidationReportFactory.createFromRDF(validationResult);
			if(endpoint!=null) {
				 validationReport.setEndpoint(endpoint);
			}else {
				 validationReport.setEndpoint("Endpoints involved in the KG");
			}
		}catch (Exception e ) {
			log.severe(e.toString());
		}
		if(validationReport!= null && !ValidationReportFactory.isSuccessfullReport(validationReport))
			update(validationReport);
		
		return validationReport;
	}

	
	
	public List<ValidationReport> getAllReports(){
		return validationRepository.findAll();
	}
	
	public Boolean remove(String reportIdStr) {
		Boolean removed = false;
		try {
			Integer reportId = Integer.valueOf(reportIdStr);
			Optional<ValidationReport> report = validationRepository.findById(reportId);
			if(report.isPresent()) {
				validationRepository.delete(report.get());
				removed = true;
			}
		}catch(Exception e) {
			log.severe(e.toString());
		}
	
		return removed;
	}
	
	public void update(ValidationReport report){
		validationRepository.save(report);
	}
	
}
