package com.eventra.exhibition.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.Formula;
import org.springframework.format.annotation.DateTimeFormat;

import com.eventra.comment.model.CommentVO;
import com.eventra.exhibitionstatus.model.ExhibitionStatus;
import com.eventra.exhibitionstatus.model.ExhibitionStatusVO;
import com.eventra.exhibitiontickettype.model.ExhibitionTicketTypeVO;
import com.eventra.exhibitor.model.ExhibitorVO;
import com.eventra.rating.model.RatingVO;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.Transient;
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
    
    @OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true) 
    private Set<ExhibitionTicketTypeVO> exhibitionTicketTypes;
    
    @OneToMany(mappedBy = "exhibition")
    private Set<CommentVO> comments;
    
    @OneToMany(mappedBy = "exhibition")
    private Set<RatingVO> ratings;

    @Column(name = "exhibition_status_id")
	private Integer exhibitionStatusId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exhibition_status_id", insertable = false, updatable = false)
    private ExhibitionStatusVO exhibitionStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibitor_id", nullable = false)
	private ExhibitorVO exhibitorVO;

	private String photoPortrait;

	private String photoLandscape;

    @Column(name = "exhibition_name")
	private String exhibitionName;

    @Column(name = "start_time")
	private LocalDateTime startTime;
    
    @Column(name = "end_time")
	private LocalDateTime endTime;

    @Column(name = "location")
	private String location;

    @Column(name = "ticket_start_time")
	private LocalDateTime ticketStartTime;

    
    @Column(name = "total_ticket_quantity")
	private Integer totalTicketQuantity;

    
    @Column(name = "sold_ticket_quantity", insertable = false, updatable = true)
	private Integer soldTicketQuantity = 0; // 20251003 柏燁

    
    @Column(name = "description", columnDefinition="LONGTEXT")
	private String description;

	private Double latitude;

	private Double longitude;


    @Column(name = "total_rating_count", insertable = false, updatable = true)
	private Integer totalRatingCount = 0; // 20251003 柏燁


    @Column(name = "total_rating_score", insertable = false, updatable = true)
	private Integer totalRatingScore = 0 ; // 20251003 柏燁
    
    @Formula("case when total_rating_count > 0"
    		+ " then ROUND(total_rating_score / total_rating_count, 1)"
    		+ " else 0 end")
    private Double averageRatingScore;

   

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

	public ExhibitionStatusVO getExhibitionStatus() {
        return exhibitionStatus;
    }

    public void setExhibitionStatus(ExhibitionStatusVO exhibitionStatus) {
        this.exhibitionStatus = exhibitionStatus;
    }

    public ExhibitorVO getExhibitorVO() {
        return exhibitorVO;
    }

    public void setExhibitorVO(ExhibitorVO exhibitorVO) {
        this.exhibitorVO = exhibitorVO;
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
	
	@Transient
	public String getSaleStatus() {
		if(this.getExhibitionStatusId() != null) {
			if(this.getExhibitionStatusId() == 6) {
				return "草稿";
			}
			if(this.getExhibitionStatusId() == 1) {
				return "待審核";
			}
			if(this.getExhibitionStatusId() == 2) {
				return "未通過審核";
			}
		}				 	
		
		LocalDateTime now = LocalDateTime.now();
		LocalDateTime start = this.getTicketStartTime(); // 開賣時間
		LocalDateTime end = this.getEndTime(); // 結束時間
		
		if(start != null && start.isBefore(now) && (end == null || end.isAfter(now))) {
			return "售票中";
		}
		if(start != null && start.isAfter(now)) {
			return "尚未開賣";
		}
		if(end != null && end.isBefore(now)) {
			return "已結束";
		}
		return "尚未開賣";
	}
	
	public int getLeftTicketQuantity() {
		int total = (this.totalTicketQuantity == null ? 0 : this.totalTicketQuantity);
		int sold = (this.soldTicketQuantity == null ? 0 : this.soldTicketQuantity);
		return Math.max(total - sold, 0);
	}
}


