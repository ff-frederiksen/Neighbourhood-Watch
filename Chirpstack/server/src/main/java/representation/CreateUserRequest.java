package representation;

public class CreateUserRequest {

	public String jwt;
	public String username;
	public String password;
	public String note;
	public String email;
	public boolean isAdmin;
	
	CreateUserRequest() {
		super();
	}
	
	CreateUserRequest(String jwt, String username, String password, String note, String email, boolean isAdmin) {
		this.jwt = jwt;
		this.username = username;
		this.password = password;
		this.note = note;
		this.email=email;
		this.isAdmin = isAdmin;
	}
}
