package com.kikkar.service;

import com.kikkar.model.Token;

public interface TokenManager {
	
	void addToken(Short token, String ip);

	Token getTokenByIpAddress(String ip);

	void deleteToken(Token token);
}
