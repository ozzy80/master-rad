package com.kikkar.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kikkar.model.Channel;
import com.kikkar.model.User;
import com.kikkar.service.ChannelManager;

@Controller
public class HomeController {

	@Autowired
	private ChannelManager channelManager;
	
	@RequestMapping(value="/")
	public String home(Model model) {
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user", user);
		return "login";
	}
		
	@RequestMapping(value="/channel")
	public String login(Principal principal, Model model) {
		if(principal != null) {
			model.addAttribute("username", principal.getName());
		}
		
		List<Channel> channelList = channelManager.getAllChannels();
		model.addAttribute("topChannels", channelList);
		
		return "channel";
	}
	
	@RequestMapping(value="/channel/{channelId}")
	public @ResponseBody Channel getChannelData(@PathVariable(value = "channelId") Long channelId) {
		return channelManager.getChannelByID(channelId);
	}
	
	/*@RequestMapping(value="/channel", method=RequestMethod.POST)
	public String addChannel(@ModelAttribute("user") Channel channel) {
		
		
		
		return "channel";
	}*/
}
