package com.eventra.search.model;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name ="search")
public class SearchVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "search_id", insertable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer searchId;
	
	@Column(name="member_id")
	private Integer memberId;
//	@ManyToOne
//	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
//	private MemberVO member;
	
	@Column(name="keyword")
	private String keyword;
	
	@Column(name="searched_at", insertable = false, updatable = false)
	private Date searchedAt;

	// 無參數建構子
	public SearchVO() {
		super();
	}
	// 有參數建構子
	public SearchVO(Integer searchId, Integer memberId, String keyword, Date searchedAt) {
		super();
		this.searchId = searchId;
		this.memberId = memberId;
		this.keyword = keyword;
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

	public Date getSearchedAt() {
		return searchedAt;
	}

	public void setSearchedAt(Date searchedAt) {
		this.searchedAt = searchedAt;
	}
	
	
}
