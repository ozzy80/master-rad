package com.kikkar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kikkar.dao.PeerInformationDao;
import com.kikkar.service.PeerInformationManager;

@Service
public class PeerInformationManagerImpl implements PeerInformationManager {

	@Autowired
	private PeerInformationDao peerInformationDao;
	

}
