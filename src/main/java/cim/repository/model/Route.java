package cim.repository.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Route {

	@Id
	private String regexPath;
	private String endpoint;
	private Boolean appendPath;
	
	public Route() {
		// empty
	}

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
	
	
	
}
