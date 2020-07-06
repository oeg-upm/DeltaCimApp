package cim.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;


@Entity
@Table(name="user")
public class User implements Serializable, Comparable<User> {
	
	@Id
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	
    @ElementCollection
    @CollectionTable(name = "tokens", 
      joinColumns = {@JoinColumn(name = "token_id", referencedColumnName = "username")})
    @MapKeyColumn(name = "token")
    @Column(name = "expiration_minutes")
	private Map<String,String> tokens;
	
	@Transient
	private static final long serialVersionUID = 1L;
	
	public User() {
		tokens = new HashMap<>();
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
	
	public Map<String, String> getTokens() {
		return tokens;
	}

	public void setTokens(Map<String, String> tokens) {
		this.tokens = tokens;
	}

	// -- Ancillary methods
	
	@Override
	public String toString() {
		return "User [username=" + username +", tokens=" + tokens + "]";
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public int compareTo(User o) {
		return this.getUsername().compareTo(o.getUsername());
	}


	
	
}
