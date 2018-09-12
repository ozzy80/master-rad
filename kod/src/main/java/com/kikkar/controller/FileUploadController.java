package com.kikkar.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.kikkar.model.Channel;
import com.kikkar.service.ChannelManager;

@Controller
public class FileUploadController {

	@Autowired
	private ChannelManager channelManager;
	
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
		channelManager.addChannel(channel);
		
		try {
			boolean savedSuccessfully = channelManager.savePicture(channelName, file);
			if (savedSuccessfully) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file");
			} else {
				return ResponseEntity.status(HttpStatus.OK).body("File Uploaded Successfully");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid file");
		}

	}
}
