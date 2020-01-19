package cim.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

import com.esotericsoftware.kryo.NotNull;

@Entity
public class XmppUser {

	@Id
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	@NotEmpty
	private String xmppDomain;
	@NotEmpty
	private String host;
	@NotNull
	private int port;
	@NotNull
	private String fileCA;
	
	public String getFileCA() {
		return fileCA;
	}
	public void setFileCA(String fileCA) {
		this.fileCA = fileCA;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getXmppDomain() {
		return xmppDomain;
	}
	public void setXmppDomain(String xmppDomain) {
		this.xmppDomain = xmppDomain;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	
	
}
