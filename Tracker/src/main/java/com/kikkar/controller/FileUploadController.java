package com.kikkar.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kikkar.dao.ChannelDao;
import com.kikkar.model.Channel;
import com.kikkar.model.User;

@Controller
public class FileUploadController {

	@Autowired
	private ChannelDao channelDao;
	
	// Handling file upload request
	@PostMapping("/channel/fileUpload")
	public ResponseEntity<Object> fileUpload(@RequestParam(value="name", required = true) String channelName, 
											@RequestParam(value="chunkSize", required = true) Integer chunkSize,
											@RequestParam(value="bitrate", required = true) Long bitrate,
											@RequestParam(value="description", required = false) String description,
											@RequestParam("file") MultipartFile file) throws IOException {

		Channel channel = new Channel();
		channel.setName(channelName);
		channel.setChunkSize(chunkSize);
		channel.setBitrate(bitrate);
		channel.setDescription(description);
		channelDao.addChannel(channel);
		
		// Save file on system
		if (!file.getOriginalFilename().isEmpty()) {
			int i = file.getOriginalFilename().lastIndexOf('.');
			String extension = null;
			if (i > 0) {
			    extension = file.getOriginalFilename().substring(i);
			}

			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(
					"C:\\Users\\Ozzy\\git\\master-rad\\Tracker\\src\\main\\webapp\\WEB-INF\\resources\\img\\channel",
					channelName + extension)));
			outputStream.write(file.getBytes());
			outputStream.flush();
			outputStream.close();
		} else {
			return new ResponseEntity<>("Invalid file.", HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>("File Uploaded Successfully.", HttpStatus.OK);
	}
}
