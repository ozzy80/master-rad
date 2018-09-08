package com.kikkar.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import com.kikkar.dao.TokenDao;
import com.kikkar.model.Token;
import com.kikkar.service.TokenManager;

@Service
public class TokenManagerIImpl implements TokenManager{

	@Autowired
	private TokenDao tokenDao;
	
	@Override
	public void addToken(Short token, HttpServletRequest request) {
		Token tokenObj = new Token();
		
		String ip = getClientIp(request);
		tokenObj.setIpAddress(ip.getBytes());
		tokenObj.setToken(token);
		
		tokenDao.addToken(tokenObj);
	}

	@Override
	public Token getTokenByIpAddress(HttpServletRequest request) {
		String ip = getClientIp(request);
		return tokenDao.getTokenByIpAddress(ip.getBytes());
	}

	@Override
	public void deleteToken(Token token) {
		tokenDao.deleteToken(token);
	}

	private String getClientIp(HttpServletRequest request) {
		String[] IP_HEADER_CANDIDATES = { 
			    "X-Forwarded-For",
			    "Proxy-Client-IP",
			    "WL-Proxy-Client-IP",
			    "HTTP_X_FORWARDED_FOR",
			    "HTTP_X_FORWARDED",
			    "HTTP_X_CLUSTER_CLIENT_IP",
			    "HTTP_CLIENT_IP",
			    "HTTP_FORWARDED_FOR",
			    "HTTP_FORWARDED",
			    "HTTP_VIA",
			    "REMOTE_ADDR" };

	    for (String header : IP_HEADER_CANDIDATES) {
	        String ip = request.getHeader(header);
	        if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
	            return ip;
	        }
	    }
	    return request.getRemoteAddr();
	}

}
