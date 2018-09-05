package com.kikkar.dao;

import com.kikkar.model.Authorities;

public interface AuthorityDao  {
	
	void updateUserRole(Authorities authority);

	Authorities getAuthorityByUsername(String username);
}
