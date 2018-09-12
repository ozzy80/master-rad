package com.kikkar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikkar.dao.TokenDao;
import com.kikkar.model.Token;
import com.kikkar.service.TokenManager;

@Service
public class TokenManagerIImpl implements TokenManager {

	@Autowired
	private TokenDao tokenDao;

	@Override
	public void addToken(Short token, String ip) {
		Token tokenObj = new Token();
		tokenObj.setIpAddress(ip.getBytes());
		tokenObj.setToken(token);

		tokenDao.addToken(tokenObj);
	}

	@Override
	public Token getTokenByIpAddress(String ip) {
		return tokenDao.getTokenByIpAddress(ip.getBytes());
	}

	@Override
	public void deleteToken(Token token) {
		tokenDao.deleteToken(token);
	}

}
