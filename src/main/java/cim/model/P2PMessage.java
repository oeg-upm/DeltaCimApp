package cim.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.google.gson.JsonObject;


@Entity
public class P2PMessage {
	
	@Id
	@Column(name="message_id")
	private String id;
	private String owner;
	private String receiver;
	private String time;
	private String request;
	@Column(columnDefinition="TEXT")
	private String message;
	private String method;
	private Boolean error;
	private Integer responseCode;
	private Boolean destroyAfterReading;

	@Column(columnDefinition="TEXT")	
	private String headers;
	
	public P2PMessage() {
		error = false;
		destroyAfterReading = false;
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
	
	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
	}


	public String getHeaders() {
		return headers;
	}

	public void setHeaders(String headers) {
		this.headers = headers;
	}
	
	

	public Boolean getDestroyAfterReading() {
		return destroyAfterReading;
	}

	public void setDestroyAfterReading(Boolean destroyAfterReading) {
		this.destroyAfterReading = destroyAfterReading;
	}


	
	@Override
	public String toString() {
		return "P2PMessage [id=" + id + ", owner=" + owner + ", receiver=" + receiver + ", time=" + time + ", request="
				+ request + ", message=" + message + ", method=" + method + ", error=" + error + ", responseCode="
				+ responseCode + ", destroyAfterReading=" + destroyAfterReading + ", headers=" + headers + "]";
	}

	public String toJsonString() {
		JsonObject jsonMessage = new JsonObject();
	 	jsonMessage.addProperty("id", getId());
	 	jsonMessage.addProperty("owner", getOwner());
	 	jsonMessage.addProperty("receiver", getReceiver());
	 	jsonMessage.addProperty("time", getTime());
	 	jsonMessage.addProperty("request", getRequest());
	 	jsonMessage.addProperty("message", getMessage());
	 	jsonMessage.addProperty("method", getMethod());
	 	jsonMessage.addProperty("error", getError());
	 	jsonMessage.addProperty("responseCode", getResponseCode());
	 	jsonMessage.addProperty("headers", getHeaders());
	 	jsonMessage.addProperty("destroy", getDestroyAfterReading());
	 	return jsonMessage.toString();
	}


	
	
}
