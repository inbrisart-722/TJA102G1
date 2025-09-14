package com.eventra.exhibitor.backend.controller.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;

/**
 * 接收使用者新增展覽輸入資料
 */
public class ExhibitionCreateDTO {

	/**
	 * 接收使用者上傳的檔案（此時尚未處理成路徑）
	 */
	private List<MultipartFile> photoPortrait;

	private List<MultipartFile> photoLandscape;
	
//	@JsonIgnore
    private Set<ExhibitionTicketTypeVO> exhibitionTicketTypes;

//	@NotBlank(message = "展覽名稱必填")
	private String exhibitionName;
	
//	@NotNull(message = "請勿空白")
	private LocalDateTime startTime;
	
//	@NotNull(message = "請勿空白")
	private LocalDateTime endTime;

//	@NotBlank(message = "展覽地點必填")
	private String location;
	
//	@NotNull(message = "請勿空白")
	private LocalDateTime ticketStartTime;

//	@NotNull(message = "必須填入總販售票數")
//	@PositiveOrZero
	private Integer totalTicketQuantity;

//	@NotBlank(message = "展覽資訊必填")
	private String description;

	
	
	public Set<ExhibitionTicketTypeVO> getExhibitionTicketTypes() {
		return exhibitionTicketTypes;
	}

	public void setExhibitionTicketTypes(Set<ExhibitionTicketTypeVO> exhibitionTicketTypes) {
		this.exhibitionTicketTypes = exhibitionTicketTypes;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDateTime startTime) {
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

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getTicketStartTime() {
		return ticketStartTime;
	}

	public void setTicketStartTime(LocalDateTime ticketStartTime) {
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
