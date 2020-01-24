package cim.xmpp.factory;

import java.util.Date;
import java.util.List;

import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

import cim.ConfigTokens;
import cim.model.ValidationReport;
import helio.framework.objects.RDF;

/**
 * This class is a Factory that helps in the validation of RDF using the Shapes designed for DELTA
 *
 */
public class ValidationReportFactory {
	
	
	// -- Constructor
	private ValidationReportFactory() {
		// empty: hides the public constructor
	}
	
	// -- Methods
	
	/**
	 * Creates a {@link ValidationReport} that specified that data was successful validated
	 * @return a successful validated {@link ValidationReport} 
	 */
	public static ValidationReport createSuccessfullReport() {
		ValidationReport report = new ValidationReport();
		Date date = new Date();
		report.setCreationDate(date.toString());
		report.setReport(ConfigTokens.VALIDATIONS_SHAPES_SUCCESS_MESSAGE);
		return report;
	}
	
	/**
	 * Creates a human-readable {@link ValidationReport} from a given {@link RDF} SHACL shape validation result
	 * @param validationResult the results of validating an RDF document against a SHACL shape
	 * @return a human-readable {@link ValidationReport}  
	 */
	public static ValidationReport createFromRDF(RDF validationResult) {
		Date now = new Date();
		ValidationReport report = new ValidationReport();
		report.setCreationDate(now.toString());
		List<RDFNode> conformsNode = validationResult.getRDF().listObjectsOfProperty(ResourceFactory.createProperty("http://www.w3.org/ns/shacl#conforms")).toList();
		if(conformsNode.size()!=1 || !conformsNode.get(0).asLiteral().getBoolean()) {
			// Error in validation
			report.setReport(formatValidationReport(validationResult));
		}else {
			report.setReport(ConfigTokens.VALIDATIONS_SHAPES_SUCCESS_MESSAGE);
		}
		return report;
	}
	
	
	private static String formatValidationReport(RDF reportRDF) {
		StringBuilder report = new StringBuilder();
		NodeIterator iterator = reportRDF.getRDF().listObjectsOfProperty(ResourceFactory.createProperty("http://www.w3.org/ns/shacl#result"));
		while(iterator.hasNext()) {
			RDFNode node = iterator.next();
			StmtIterator statements = reportRDF.getRDF().listStatements(node.asResource(), null, (RDFNode) null);
			while(statements.hasNext()) {
				Statement statement = statements.next();
				String predicate = statement.asTriple().getPredicate().toString();
				String object = statement.asTriple().getObject().toString();
				if(predicate.contains("http://www.w3.org/ns/shacl#resultSeverity"))
					report.append("Type: ").append(object).append(";\n");
				if(predicate.contains("http://www.w3.org/ns/shacl#sourceConstraintComponent"))
					report.append("Contraint : ").append(object).append(";\n");
				if(predicate.contains("http://www.w3.org/ns/shacl#resultPath"))
					report.append("Property involved: ").append(object).append(";\n");
				if(predicate.contains("http://www.w3.org/ns/shacl#resultMessage"))
					report.append("Error message: ").append(object).append(";\n");
				if(predicate.contains("http://www.w3.org/ns/shacl#focusNode"))
					report.append("RDF subject: ").append(object).append(";\n");			
			}
		}
		
		return report.toString().replace("http://www.w3.org/ns/shacl#", "");
	}
	
	/**
	 * This method establishes when a {@link ValidationReport} is success or not
	 * @param report a {@link ValidationReport}
	 * @return if the a {@link ValidationReport} is success, true, or not, false
	 */
	public static boolean isSuccessfullReport(ValidationReport report) {
		return report!=null && report.getReport().contains(ConfigTokens.VALIDATIONS_SHAPES_SUCCESS_MESSAGE);
	}

}
