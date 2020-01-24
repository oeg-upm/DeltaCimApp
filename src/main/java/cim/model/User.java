package cim.model;

import java.util.Set;
import javax.persistence.JoinColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

@Entity
public class User {
	
	@Id
	@NotEmpty
	private String username;
	@NotEmpty
	private String password;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="authorities_users",
	joinColumns=@JoinColumn(name="usuario_id"),
	inverseJoinColumns=@JoinColumn(name="authority_id"))
	private Set<Authority> authority;
	
	//No persistent variable. It will not be save in the database, just for provide information to Set<Authority>.
	@Transient
	private String authorityTemp;
	
	public String getAuthorityTemp() {
		return authorityTemp;
	}

	public void setAuthorityTemp(String authorityTemp) {
		this.authorityTemp = authorityTemp;
	}

	public User() {
		
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

	public Set<Authority> getAuthority() {
		return authority;
	}

	public void setAuthority(Set<Authority> authority) {
		this.authority = authority;
	}
	
	//Get the authority of an user and replace with the correspondent authority
	public String getAuthorities(User user) {
		Set<Authority> set = user.getAuthority();
		String authorities = "";
		for (Authority s : set) {
			authorities += s.getAuthority();
		}
		authorities = authorities.replaceAll("ROLE_USER", "USER");
		authorities = authorities.replaceAll("ROLE_ADMINUSER", "ADMIN");
		authorities = authorities.replaceAll("USERROLE_ADMIN", "ADMIN");
		return authorities;
	}
	
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", authority=" + authority + "]";
	}


	
	
}
