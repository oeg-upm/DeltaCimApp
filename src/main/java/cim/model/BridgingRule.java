package cim.model;

import java.io.Serializable;

import cim.ConfigTokens;
import cim.factory.InteroperabilityModuleFactory;
import cim.model.enums.Method;
import helio.framework.objects.Tuple;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class BridgingRule implements Serializable{
	
	// --- Attributes
	
	@Transient
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO) 
	private long id;
	
	@NotEmpty
	private String xmppPattern;
	
	
	private String endpoint;

	@NotNull
	private Boolean appendPath;
	
	@NotNull
	private Method method;
	

	private String interoperabilityModuleFile;
	
	@Column(columnDefinition="TEXT")
	private String readingMapping;
	@Column(columnDefinition="TEXT")
	private String writtingMapping;
	
	// --- Constructor
	
	public BridgingRule() {
		// empty
	}
	
	// --- Getters & Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getXmppPattern() {
		return xmppPattern;
	}

	public void setXmppPattern(String xmppPattern) {
		this.xmppPattern = xmppPattern;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public Boolean getAppendPath() {
		return appendPath;
	}

	public void setAppendPath(Boolean appendPath) {
		this.appendPath = appendPath;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public String getInteroperabilityModuleFile() {
		return interoperabilityModuleFile;
	}

	public void setInteroperabilityModuleFile(String interoperabilityModuleFile) {
		this.interoperabilityModuleFile = interoperabilityModuleFile;
	}

	public String getReadingMapping() {
		return readingMapping;
	}

	public void setReadingMapping(String readingMapping) {
		this.readingMapping = readingMapping;
	}

	public String getWrittingMapping() {
		return writtingMapping;
	}

	public void setWrittingMapping(String writtingMapping) {
		this.writtingMapping = writtingMapping;
	}

	
	
	// --- Auxiliary methods



	
	@Override
	public String toString() {
		return "BridgingRule [regexPath=" + xmppPattern + ", endpoint=" + endpoint + ", appendPath=" + appendPath
				+ ", method=" + method + ", mappingsFolder=" + interoperabilityModuleFile + ", readingMapping=" + readingMapping
				+ ", writtingMapping=" + writtingMapping + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BridgingRule other = (BridgingRule) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void updateMappingContent() {
		if(getInteroperabilityModuleFile()!=null) {
			Tuple<String,String> module = InteroperabilityModuleFactory.readInteroperabilityModule(ConfigTokens.MODULES_BASE_DIR+getInteroperabilityModuleFile());
			if(module!=null && !module.getFirstElement().isEmpty())
				this.readingMapping = module.getFirstElement();
			if(module!=null && !module.getSecondElement().isEmpty())
				this.writtingMapping = module.getSecondElement();
		}
	}

	
}
