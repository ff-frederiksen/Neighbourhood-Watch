package user;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
	private String id;
	private String username;
	private String homeID;
	private String jwt;
	private boolean isAdmin;
 
 	public User(String id, String username, String homeID) {
 		this.id = id;
 		this.setUsername(username);
 		this.setHomeID(homeID);
 		setAdmin(false);
 	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHouses() {
		return homeID;
	}

	public void setHomeID(String homeID) {
		this.homeID = homeID;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
 
}
