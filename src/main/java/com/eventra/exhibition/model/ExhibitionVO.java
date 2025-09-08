package com.eventra.exhibition.model;

import com.eventra.exhibitor.model.ExhibitorVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Entity
@Table(name = "Exhibition")
public class ExhibitionVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_id")
	private Integer exhibitionId;

    @Column(name = "exhibition_status_id")
	private Integer exhibitionStatusId;

    @NotNull(message = "展商必須填寫")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibitor_id", nullable = false)
	private ExhibitorVO exhibitorId;

	private String photoPortrait;

	private String photoLandscape;

    @NotBlank(message = "展覽名稱必填")
    @Column(name = "exhibition_name")
	private String exhibitionName;

    @NotNull(message = "請勿空白")
    @Column(name = "start_time")
	private LocalDateTime startTime;

    @NotNull(message = "請勿空白")
    @Column(name = "end_time")
	private LocalDateTime endTime;

    @NotBlank(message = "展覽地點必填")
    @Column(name = "location")
	private String location;

    @NotNull(message = "請勿空白")
    @Column(name = "ticket_start_time")
	private LocalDateTime ticketStartTime;

    @NotNull(message = "必須填入總販售票數")
    @PositiveOrZero
    @Column(name = "total_ticket_quantity")
	private Integer totalTicketQuantity;

    @NotNull
    @PositiveOrZero
    @Column(name = "sold_ticket_quantity")
	private Integer soldTicketQuantity;

    @NotBlank(message = "展覽資訊必填")
    @Column(name = "description")
	private String description;


	private Double latitude;

	private Double longitude;

    @NotNull
    @Column(name = "rating_count")
	private Integer ratingCount = 0;

    @NotNull
    @Column(name = "average_rating_score")
	private Double averageRatingScore = 0.0;

    public ExhibitionVO() {
        super();
    }

    public ExhibitionVO(Integer exhibitionId, Integer exhibitionStatusId, ExhibitorVO exhibitorId, String photoLandscape, String photoPortrait, String exhibitionName, LocalDateTime startTime, Double averageRatingScore, Integer ratingCount, Double longitude, Double latitude, String description, Integer soldTicketQuantity, Integer totalTicketQuantity, LocalDateTime ticketStartTime, String location, LocalDateTime endTime) {
        this.exhibitionId = exhibitionId;
        this.exhibitionStatusId = exhibitionStatusId;
        this.exhibitorId = exhibitorId;
        this.photoLandscape = photoLandscape;
        this.photoPortrait = photoPortrait;
        this.exhibitionName = exhibitionName;
        this.startTime = startTime;
        this.averageRatingScore = averageRatingScore;
        this.ratingCount = ratingCount;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.soldTicketQuantity = soldTicketQuantity;
        this.totalTicketQuantity = totalTicketQuantity;
        this.ticketStartTime = ticketStartTime;
        this.location = location;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Integer getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(Integer exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public Integer getExhibitionStatusId() {
        return exhibitionStatusId;
    }

    public void setExhibitionStatusId(Integer exhibitionStatusId) {
        this.exhibitionStatusId = exhibitionStatusId;
    }

    public ExhibitorVO getExhibitorId() {
        return exhibitorId;
    }

    public void setExhibitorId(ExhibitorVO exhibitorId) {
        this.exhibitorId = exhibitorId;
    }

    public String getPhotoPortrait() {
        return photoPortrait;
    }

    public void setPhotoPortrait(String photoPortrait) {
        this.photoPortrait = photoPortrait;
    }

    public String getPhotoLandscape() {
        return photoLandscape;
    }

    public void setPhotoLandscape(String photoLandscape) {
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

    public Integer getSoldTicketQuantity() {
        return soldTicketQuantity;
    }

    public void setSoldTicketQuantity(Integer soldTicketQuantity) {
        this.soldTicketQuantity = soldTicketQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.ratingCount = ratingCount;
    }

    public Double getAverageRatingScore() {
        return averageRatingScore;
    }

    public void setAverageRatingScore(Double averageRatingScore) {
        this.averageRatingScore = averageRatingScore;
    }
}


