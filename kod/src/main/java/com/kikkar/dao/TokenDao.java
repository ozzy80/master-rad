package com.kikkar.dao;

import com.kikkar.model.Token;

public interface TokenDao {
	void addToken(Token token);

	Token getTokenByIpAddress(byte[] ipAddress);

	void deleteToken(Token token);

}
