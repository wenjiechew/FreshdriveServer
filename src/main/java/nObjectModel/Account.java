package nObjectModel;

/**
 * Account object entity 
 * Contains fields which can hold all user's information, and accessed through getters and setters.
 * 
 * @author ottoma
 *
 */
public class Account {
	private String _id;
	private String username;
	private String email;
	private String password;
	private String token;

	public String get_id() {
		return new String(_id);
	}

	public String getUsername() {
		return new String(username);
	}

	public String getPassword() {
		return new String(password);
	}

	
	public void set_id(String _id) {
		this._id = _id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return new String(email);
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return new String(token);
	}

	public void setToken(String token) {
		this.token = token;
	}

	
}
