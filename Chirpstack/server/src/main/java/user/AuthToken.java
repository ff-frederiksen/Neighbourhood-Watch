package user;

public class AuthToken {
	private String username;
	private String jwt;
	
	public AuthToken(String user, String jwt) {
		this.username = user;
		this.jwt = jwt;
	}
	
	public AuthToken() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public String getJwt() {
		return jwt;
	}
}
