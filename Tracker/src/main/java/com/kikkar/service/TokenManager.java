package com.kikkar.service;

import javax.servlet.http.HttpServletRequest;

import com.kikkar.model.Token;

public interface TokenManager {
	void addToken(Short token, HttpServletRequest request);

	Token getTokenByIpAddress(HttpServletRequest request);

	void deleteToken(Token token);
}
