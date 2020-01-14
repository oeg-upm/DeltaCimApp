package cim.repository.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Route {

	@Id
	private String regexPath;
	@NotEmpty
	private String endpoint;
	@NotNull
	private Boolean appendPath;
	
	public Route() {
		// empty
		appendPath=false;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Route [regexPath=" + regexPath + ", endpoint=" + endpoint + ", appendPath=" + appendPath + "]";
	}
	
	
	
}
