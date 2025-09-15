package com.eventra.exhibition.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.Formula;

import com.eventra.comment.model.CommentVO;
import com.eventra.exhibition_ticket_type.model.ExhibitionTicketTypeVO;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.rating.model.RatingVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "Exhibition")
public class ExhibitionVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_id")
	private Integer exhibitionId;
    
    @JsonIgnore
    @OneToMany(mappedBy = "exhibition")
    private Set<ExhibitionTicketTypeVO> exhibitionTicketTypes;
    
    @JsonIgnore
    @OneToMany(mappedBy = "exhibition")
    private Set<CommentVO> comments;
    
    @JsonIgnore
    @OneToMany(mappedBy = "exhibition")
    private Set<RatingVO> ratings;

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

    @NotNull
    @PositiveOrZero
    @Column(name = "sold_ticket_quantity")
	private Integer soldTicketQuantity;

    @NotBlank(message = "展覽資訊必填")
    @Column(name = "description", columnDefinition="LONGTEXT")
	private String description;

	private Double latitude;

	private Double longitude;

    @NotNull
    @Column(name = "total_rating_count")
	private Integer totalRatingCount;

    @NotNull
    @Column(name = "total_rating_score")
	private Integer totalRatingScore;
    
    @Formula("case when total_rating_count > 0"
    		+ " then ROUND(total_rating_score / total_rating_count, 1)"
    		+ " else 0 end")
    private Double averageRatingScore;

    public ExhibitionVO() {
        super();
    }

	public ExhibitionVO(Integer exhibitionId, Integer exhibitionStatusId, ExhibitorVO exhibitorId, String photoLandscape, String photoPortrait, String exhibitionName, Timestamp startTime, Integer totalRatingScore, Integer totalRatingCount, Double longitude, Double latitude, String description, Integer soldTicketQuantity, Integer totalTicketQuantity, Timestamp ticketStartTime, String location, Timestamp endTime) {
        this.exhibitionId = exhibitionId;
        this.exhibitionStatusId = exhibitionStatusId;
        this.exhibitorId = exhibitorId;
        this.photoLandscape = photoLandscape;
        this.photoPortrait = photoPortrait;
        this.exhibitionName = exhibitionName;
        this.startTime = startTime;
        this.totalRatingScore = totalRatingScore;
        this.totalRatingCount = totalRatingCount;
        this.longitude = longitude;
        this.latitude = latitude;
        this.description = description;
        this.soldTicketQuantity = soldTicketQuantity;
        this.totalTicketQuantity = totalTicketQuantity;
        this.ticketStartTime = ticketStartTime;
        this.location = location;
        this.endTime = endTime;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
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
        return totalRatingCount;
    }

    public void setRatingCount(Integer ratingCount) {
        this.totalRatingCount = ratingCount;
    }

    public Integer getTotalRatingScore() {
        return totalRatingScore;
    }

    public void setTotalRatingScore(Integer totalRatingScore) {
        this.totalRatingScore = totalRatingScore;
    }

	public Set<ExhibitionTicketTypeVO> getExhibitionTicketTypes() {
		return exhibitionTicketTypes;
	}

	public void setExhibitionTicketTypes(Set<ExhibitionTicketTypeVO> exhibitionTicketTypes) {
		this.exhibitionTicketTypes = exhibitionTicketTypes;
	}

	public Set<CommentVO> getComments() {
		return comments;
	}

	public void setComments(Set<CommentVO> comments) {
		this.comments = comments;
	}

	public Set<RatingVO> getRatings() {
		return ratings;
	}

	public void setRatings(Set<RatingVO> ratings) {
		this.ratings = ratings;
	}

	public Integer getTotalRatingCount() {
		return totalRatingCount;
	}

	public void setTotalRatingCount(Integer totalRatingCount) {
		this.totalRatingCount = totalRatingCount;
	}
    public Double getAverageRatingScore() {
		return averageRatingScore;
	}

	public void setAverageRatingScore(Double averageRatingScore) {
		this.averageRatingScore = averageRatingScore;
	}
}


