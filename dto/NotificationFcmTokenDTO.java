package com.carrus.statsca.dto;

import java.time.LocalDateTime;

import com.carrus.statsca.admin.entity.NotificationFcmToken.EnumStatut;




public class NotificationFcmTokenDTO {

	private Long id;
	private Boolean seen;
	private LocalDateTime sendDate;
	private LocalDateTime maxSendingDate;
	private EnumStatut statut;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getSeen() {
		return seen;
	}

	public void setSeen(Boolean seen) {
		this.seen = seen;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public LocalDateTime getMaxSendingDate() {
		return maxSendingDate;
	}

	public void setMaxSendingDate(LocalDateTime maxSendingDate) {
		this.maxSendingDate = maxSendingDate;
	}

	public EnumStatut getStatut() {
		return statut;
	}

	public void setStatut(EnumStatut statut) {
		this.statut = statut;
	}

}