package com.kikkar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "authorities")
public class Authorities {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "authorityId")
	private Integer authorityId;

	@NotNull
	@Column(name = "authority", length = 45)
	private String authority;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "username")
	private User username;

	public Integer getAuthorityId() {
		return authorityId;
	}

	public void setAuthorityId(Integer authorityId) {
		this.authorityId = authorityId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

	public User getUser() {
		return username;
	}

	public void setUser(User user) {
		this.username = user;
	}

}
