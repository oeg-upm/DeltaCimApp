package cim.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cim.ConfigTokens;
import cim.factory.ValidationReportFactory;
import cim.model.ValidationReport;
import cim.repository.ValidationReportRepository;
import helio.framework.objects.RDF;

@Service
public class ValidationService {

	@Autowired
	public ValidationReportRepository validationRepository;
	private static Logger log = Logger.getLogger(ValidationService.class.getName());
	private String validationShapes;
		
	
	
	
	public List<ValidationReport> getAllReports(){
		return validationRepository.findAll();
	}
	
	public void remove(String reportIdStr) {
		try {
			Integer reportId = Integer.valueOf(reportIdStr);
			Optional<ValidationReport> report = validationRepository.findById(reportId);
			if(report.isPresent()) {
				validationRepository.delete(report.get());
			}
		}catch(Exception e) {
			log.severe(e.toString());
		}		
	}
	
	public void update(ValidationReport report){
		validationRepository.save(report);
	}
	
	public ValidationReport validateRDF(String rdfDocument, String format) {
		return generateValidationReport(rdfDocument, format, null);
	}
	
	public ValidationReport generateValidationReport(String rdfDocument, String format, String endpoint) {
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
			return validationReport;
	}
	
	public void readValidationShapesFile() {
		validationShapes = readFile(ConfigTokens.VALIDATIONS_SHAPES_FILE);
	}
			
	private String readFile(String fileName) {
		StringBuilder data = new StringBuilder();
		FileReader file = null;
		BufferedReader bf = null;
		try {
			file = new FileReader(fileName);
			bf = new BufferedReader(file);
			bf.lines().forEach(line -> data.append(line).append("\n"));
		} catch (Exception e) {
			log.severe(e.toString());
		} finally {
			try {
				if (bf != null)
					bf.close();
				if (file != null)
					file.close();
			} catch (Exception e) {
				log.severe(e.toString());
			}
		}
		return data.toString();
	}
}
