package cc.cnplay.uhf;

import org.json.JSONException;
import org.json.JSONObject;

public class Login {
	private String id;
	private String username;
	private String password;
	private String hostname;
	private String reftoken;
	private String acctoken;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getReftoken() {
		return reftoken;
	}

	public void setReftoken(String reftoken) {
		this.reftoken = reftoken;
	}

	public String getAcctoken() {
		return acctoken;
	}

	public void setAcctoken(String acctoken) {
		this.acctoken = acctoken;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", this.getId());
		json.put("username", this.getUsername());
		json.put("password", this.getPassword());
		json.put("hostname", this.getHostname());
		json.put("reftoken", this.getReftoken());
		json.put("acctoken", this.getAcctoken());
		return json;
	}

}
