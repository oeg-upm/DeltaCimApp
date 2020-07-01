package cim.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

import com.esotericsoftware.kryo.NotNull;

@Entity
public class XmppUser implements Serializable{

	@Transient
	private static final long serialVersionUID = 1L;
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
	
	
	@Override
	public String toString() {
		return "XmppUser [username=" + username + ", password=" + password + ", xmppDomain=" + xmppDomain + ", host="
				+ host + ", port=" + port + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + port;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		result = prime * result + ((xmppDomain == null) ? 0 : xmppDomain.hashCode());
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
		XmppUser other = (XmppUser) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (port != other.port)
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		if (xmppDomain == null) {
			if (other.xmppDomain != null)
				return false;
		} else if (!xmppDomain.equals(other.xmppDomain))
			return false;
		return true;
	}
	
	
	
	
}
