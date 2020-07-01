package cim.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import cim.ConfigTokens;
import cim.factory.InteroperabilityModuleFactory;
import cim.model.enums.Method;
import helio.framework.objects.Tuple;

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
	
	@NotNull
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((xmppPattern == null) ? 0 : xmppPattern.hashCode());
		return result;
	}

	
	@Override
	public String toString() {
		return "BridgingRule [regexPath=" + xmppPattern + ", endpoint=" + endpoint + ", appendPath=" + appendPath
				+ ", method=" + method + ", mappingsFolder=" + interoperabilityModuleFile + ", readingMapping=" + readingMapping
				+ ", writtingMapping=" + writtingMapping + "]";
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
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (xmppPattern == null) {
			if (other.xmppPattern != null)
				return false;
		} else if (!xmppPattern.equals(other.xmppPattern))
			return false;
		return true;
	}
	

	public void updateMappingContent() {
		if(getInteroperabilityModuleFile()!=null) {
			Tuple<String,String> module = InteroperabilityModuleFactory.readInteroperabilityModule(ConfigTokens.MODULES_BASE_DIR+getInteroperabilityModuleFile());
			this.readingMapping = module.getFirstElement();
			this.writtingMapping = module.getSecondElement();
		}
	}

	
}
