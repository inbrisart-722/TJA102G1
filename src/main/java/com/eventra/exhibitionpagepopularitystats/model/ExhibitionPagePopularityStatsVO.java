package com.eventra.exhibitionpagepopularitystats.model;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="exhibition_page_popularity_stats")
public class ExhibitionPagePopularityStatsVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name ="exhibition_id")
	private Integer exhibitionId;
	
	@Column(name ="view_date", insertable = false)
	private Date viewDate;
	
	@Column(name ="exhibition_page_view_count", insertable = false)
	private Integer exhibitionPageViewCount;

	// 無參數建構子
	public ExhibitionPagePopularityStatsVO() {
		super();
	}

	// 有參數建構子
	public ExhibitionPagePopularityStatsVO(Integer id, Integer exhibitionId, Date viewDate,
			Integer exhibitionPageViewCount) {
		super();
		this.id = id;
		this.exhibitionId = exhibitionId;
		this.viewDate = viewDate;
		this.exhibitionPageViewCount = exhibitionPageViewCount;
	}

	// getter/setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getExhibitionId() {
		return exhibitionId;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public Date getViewDate() {
		return viewDate;
	}

	public void setViewDate(Date viewDate) {
		this.viewDate = viewDate;
	}

	public Integer getExhibitionPageViewCount() {
		return exhibitionPageViewCount;
	}

	public void setExhibitionPageViewCount(Integer exhibitionPageViewCount) {
		this.exhibitionPageViewCount = exhibitionPageViewCount;
	}
	
}
