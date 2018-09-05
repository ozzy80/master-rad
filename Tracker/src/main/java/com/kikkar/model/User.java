package com.kikkar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user")
public class User {


	@Id
	@NotNull
	@Column(name = "username", length = 50)
	private String username;
	
	@NotNull
	@Column(name = "password", length = 255)
	private String password;

	@NotNull
	@Column(name = "email", length = 255)
	private String email;

	@NotNull
	@Column(name = "enabled")
	private Boolean enabled;
	
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

}
