package cim.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class BridgingRule {
	
	// --- Attributes
	
	@Id
	private String regexPath;
	
	@NotEmpty
	private String endpoint;

	@NotNull
	private Boolean appendPath;
	
	@NotNull
	private String mappingsFolder;
	
	@Column(columnDefinition="TEXT")
	private String readingMapping;
	@Column(columnDefinition="TEXT")
	private String writtingMapping;
	
	// --- Constructor
	
	public BridgingRule() {
		appendPath=false;
	}
	
	// --- Getters & Setters

	public String getRegexPath() {
		return regexPath;
	}

	public void setRegexPath(String regexPath) {
		this.regexPath = regexPath;
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

	public String getMappingsFolder() {
		return mappingsFolder;
	}

	public void setMappingsFolder(String mappingsFolder) {
		this.mappingsFolder = mappingsFolder;
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
		result = prime * result + ((regexPath == null) ? 0 : regexPath.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "BridgingRule [regexPath=" + regexPath + ", endpoint=" + endpoint + ", appendPath=" + appendPath
				+ ", mappingsFolder=" + mappingsFolder + ", readingMapping=" + readingMapping + ", writtingMapping="
				+ writtingMapping + "]";
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
		if (regexPath == null) {
			if (other.regexPath != null)
				return false;
		} else if (!regexPath.equals(other.regexPath))
			return false;
		return true;
	}
	
}
