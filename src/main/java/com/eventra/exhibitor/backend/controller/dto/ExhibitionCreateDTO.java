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
	
	private Integer exhibitionId;
	
	private Boolean draft;
	
	private String ticketJson;
	
	/**
	 * 接收使用者上傳的檔案（此時尚未處理成路徑）
	 */
	private MultipartFile photoPortrait;

	private MultipartFile photoLandscape;
	
	/**
	 * 進入編輯頁面時給舊有圖片預覽用
	 */
	private String photoPortraitPath;
	
	private String photoLandscapePath;
	
//	@JsonIgnore
    private Set<ExhibitionTicketTypeVO> exhibitionTicketTypes;

//	@NotBlank(message = "展覽名稱必填")
	private String exhibitionName;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//	@NotNull(message = "請勿空白")
	private LocalDateTime startTime;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//	@NotNull(message = "請勿空白")
	private LocalDateTime endTime;

//	@NotBlank(message = "展覽地點必填")
	private String location;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
//	@NotNull(message = "請勿空白")
	private LocalDateTime ticketStartTime;

//	@NotNull(message = "必須填入總販售票數")
//	@PositiveOrZero
	private Integer totalTicketQuantity;

//	@NotBlank(message = "展覽資訊必填")
	private String description;
	
	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}
	
	public Boolean getDraft() {
		return draft;
	}

	public void setDraft(Boolean draft) {
		this.draft = draft;
	}

	public String getTicketJson() {
		return ticketJson;
	}

	public void setTicketJson(String ticketJson) {
		this.ticketJson = ticketJson;
	}

	public String getPhotoPortraitPath() {
		return photoPortraitPath;
	}

	public void setPhotoPortraitPath(String photoPortraitPath) {
		this.photoPortraitPath = photoPortraitPath;
	}

	public String getPhotoLandscapePath() {
		return photoLandscapePath;
	}

	public void setPhotoLandscapePath(String photoLandscapePath) {
		this.photoLandscapePath = photoLandscapePath;
	}

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

	public MultipartFile getPhotoPortrait() {
		return photoPortrait;
	}

	public void setPhotoPortrait(MultipartFile photoPortrait) {
		this.photoPortrait = photoPortrait;
	}

	public MultipartFile getPhotoLandscape() {
		return photoLandscape;
	}

	public void setPhotoLandscape(MultipartFile photoLandscape) {
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
