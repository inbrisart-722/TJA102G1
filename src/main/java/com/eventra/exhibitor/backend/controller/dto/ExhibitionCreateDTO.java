package com.eventra.exhibitor.backend.controller.dto;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * 接收使用者新增展覽輸入資料
 */
public class ExhibitionCreateDTO {

	/**
	 * 接收使用者上傳的檔案（此時尚未處理成路徑）
	 */
	private List<MultipartFile> photoPortrait;

	private List<MultipartFile> photoLandscape;

	@NotBlank(message = "展覽名稱必填")
	@Column(name = "exhibition_name")
	private String exhibitionName;

	@NotNull(message = "請勿空白")
	@Column(name = "start_time")
	private Timestamp startTime;

	@NotNull(message = "請勿空白")
	@Column(name = "end_time")
	private Timestamp endTime;

	@NotBlank(message = "展覽地點必填")
	@Column(name = "location")
	private String location;

	@NotNull(message = "請勿空白")
	@Column(name = "ticket_start_time")
	private Timestamp ticketStartTime;

	@NotNull(message = "必須填入總販售票數")
	@PositiveOrZero
	@Column(name = "total_ticket_quantity")
	private Integer totalTicketQuantity;

	@NotBlank(message = "展覽資訊必填")
	@Column(name = "description", columnDefinition = "LONGTEXT")
	private String description;

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public List<MultipartFile> getPhotoPortrait() {
		return photoPortrait;
	}

	public void setPhotoPortrait(List<MultipartFile> photoPortrait) {
		this.photoPortrait = photoPortrait;
	}

	public List<MultipartFile> getPhotoLandscape() {
		return photoLandscape;
	}

	public void setPhotoLandscape(List<MultipartFile> photoLandscape) {
		this.photoLandscape = photoLandscape;
	}

	public String getExhibitionName() {
		return exhibitionName;
	}

	public void setExhibitionName(String exhibitionName) {
		this.exhibitionName = exhibitionName;
	}

	public Timestamp getEndTime() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Timestamp getTicketStartTime() {
		return ticketStartTime;
	}

	public void setTicketStartTime(Timestamp ticketStartTime) {
		this.ticketStartTime = ticketStartTime;
	}

	public Integer getTotalTicketQuantity() {
		return totalTicketQuantity;
	}

	public void setTotalTicketQuantity(Integer totalTicketQuantity) {
		this.totalTicketQuantity = totalTicketQuantity;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
