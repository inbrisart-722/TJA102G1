package com.eventra.search.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table (name ="search")
public class SearchVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "search_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer searchId;
	
	@Column(name = "member_id")
    private Integer memberId;

    @Column(name = "keyword")
    private String keyword;

    @Column(name = "searched_at", insertable = false, updatable = false)
    private Timestamp searchedAt;
    
    // 新增欄位, 因應前端搜尋頁面設計需求
    @Column(name = "regions", nullable = false)
    private String regions = "";  // 預設空字串
    
    @Column(name = "date_from")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateFrom; 
    
    @Column(name = "date_to")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateTo; 

	// 無參數建構子
	public SearchVO() {
		super();
	}

	// 有參數建構子
	public SearchVO(Integer searchId, Integer memberId, String keyword, String regions, Date dateFrom, Date dateTo,
			Timestamp searchedAt) {
		super();
		this.searchId = searchId;
		this.memberId = memberId;
		this.keyword = keyword;
		this.regions = regions;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
		this.searchedAt = searchedAt;
	}
	
	// getter/setter
	public Integer getSearchId() {
		return searchId;
	}

	public void setSearchId(Integer searchId) {
		this.searchId = searchId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getRegions() {
		return regions;
	}

	public void setRegions(String regions) {
		this.regions = regions;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Timestamp getSearchedAt() {
		return searchedAt;
	}

	public void setSearchedAt(Timestamp searchedAt) {
		this.searchedAt = searchedAt;
	}
}
