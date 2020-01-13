package cim.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class P2PMessage {
	@Id
	private String id;
	
	private String owner;
	private String receiver;
	private String time;
	private String request;
	@Column(columnDefinition="TEXT")
	private String message;
	private String method;
	private Boolean error;
	
	public P2PMessage() {
		error = false;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getReceiver() {
		return receiver;
	}
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "P2PMessage [id=" + id + ", owner=" + owner + ", receiver=" + receiver + ", time=" + time + ", request="
				+ request + ", message=" + message + ", method=" + method + ", error=" + error + "]";
	}
		
	
	
}
