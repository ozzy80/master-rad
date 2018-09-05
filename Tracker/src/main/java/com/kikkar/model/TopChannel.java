package com.kikkar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "top_channel")
public class TopChannel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "top_channel_id")
	private Integer topChannelId;
	
	@NotNull
	@Column(name = "views_number")
	private Long viewsNumber;

	@NotNull
	@ManyToOne
	@JoinColumn(name="channel_id")
	private Channel channel;

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Long getViewsNumber() {
		return viewsNumber;
	}

	public void setViewsNumber(Long viewsNumber) {
		this.viewsNumber = viewsNumber;
	}
	
}

