package com.eventra.comment.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.Formula;

import com.eventra.comment.controller.CommentStatus;
import com.eventra.comment_reaction.model.CommentReactionVO;
import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.member.model.MemberVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "`comment`")
public class CommentVO implements Serializable {
	// @Expose -> Gson | @JsonIgnore -> Jackson for FKs

	@Id
	@Column(name = "comment_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer commentId;

	// 「這個屬性在 Java 程式中是 Enum 類型，但在儲存到資料庫時，JPA 會將它的值轉換成 String 存進去。」
	@Enumerated(EnumType.STRING)
	@Column(name = "comment_status", insertable = false)
	private CommentStatus commentStatus;

//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//	@Column(name = "exhibition_id", insertable = false, updatable = false)
//	private Integer exhibitionId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "exhibition_id", referencedColumnName = "exhibition_id", nullable = false)
	private ExhibitionVO exhibition;

//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
//	@Column(name = "member_id", insertable = false, updatable = false)
//	private Integer memberId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private MemberVO member;

//	@Column(name = "parent_comment_id", insertable = false, updatable = false)
//	private Integer parentCommentId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id", referencedColumnName = "comment_id", nullable = true)
	private CommentVO parentComment;

	@JsonIgnore
	@OneToMany(mappedBy = "parentComment") // 用 db cascade
	private Set<CommentVO> childComments;

//	@Transient // 告訴 Hibernate 這個屬性不是資料庫欄位，不需要映射 => 靠 @Formula 也可
	@Formula("(select count(*) from comment c2 where c2.parent_comment_id = comment_id)") // 觸發時機：每次載入該實體時 重新計算。
	private Integer childCommentsCount;

	@OneToMany(mappedBy = "comment")
	private Set<CommentReactionVO> commentReactions;

	@Column(name = "like_count", insertable = false)
	private Integer likeCount;

	@Column(name = "dislike_count", insertable = false)
	private Integer dislikeCount;

	@Column(name = "content", updatable = false)
	private String content;

//	@CreationTimestamp
//	@JsonFormat(pattern = "yyyy-MM-dd") // 輸出只用到這格式
	@Column(name = "created_at", insertable = false, updatable = false)
	private Timestamp createdAt;

//	@UpdateTimestamp
	@JsonFormat(pattern = "yyyy-MM-dd") // 輸出只用到這格式
	@Column(name = "updated_at", insertable = false, updatable = false)
	private Timestamp updatedAt;

	public CommentVO getParentComment() {
		return parentComment;
	}

	public void setParentComment(CommentVO parentComment) {
		this.parentComment = parentComment;
//		this.parentCommentId = (parentComment != null ? parentComment.getCommentId() : null);
	}

//	public Integer getParentCommentId() {
//		return parentCommentId;
//	}
//	public void setParentCommentId(Integer parentCommentId) {
//		this.parentCommentId = parentCommentId;
//	}
	public Set<CommentVO> getChildComments() {
		return childComments;
	}

	public void setChildComments(Set<CommentVO> childComments) {
		this.childComments = childComments;
	}

	public Integer getCommentId() {
		return commentId;
	}

	public void setCommentId(Integer commentId) {
		this.commentId = commentId;
	}

	public CommentStatus getCommentStatus() {
		return commentStatus;
	}

	public void setCommentStatus(CommentStatus commentStatus) {
		this.commentStatus = commentStatus;
	}

//	public Integer getExhibitionId() {
//		return exhibitionId;
//	}
//	public void setExhibitionId(Integer exhibitonId) {
//		this.exhibitionId = exhibitonId;
//	}
	public ExhibitionVO getExhibition() {
		return exhibition;
	}

	public void setExhibition(ExhibitionVO exhibition) {
		this.exhibition = exhibition;
//		this.exhibitionId = (exhibition != null ? exhibition.getExhibitionId() : null);
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public Integer getDislikeCount() {
		return dislikeCount;
	}

	public void setDislikeCount(Integer dislikeCount) {
		this.dislikeCount = dislikeCount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Timestamp getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Timestamp updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Set<CommentReactionVO> getCommentReactions() {
		return commentReactions;
	}

	public void setCommentReactions(Set<CommentReactionVO> commentReactions) {
		this.commentReactions = commentReactions;
	}

	public MemberVO getMember() {
		return member;
	}

	public void setMember(MemberVO member) {
		this.member = member;
//		this.memberId = (member != null ? member.getMemberId() : null);
	}
	
//	public Integer getMemberId() {
//		return memberId;
//	}
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}

	// toString 一直害我 circular reference，此處也不能帶入 onetomany + manytoone（同時）
	// StackOverflowError
//	@Override
//	public String toString() {
//		return "CommentVO [commentId=" + commentId + ", commentStatus=" + commentStatus + ", exhibitionId="
//				+ exhibitionId + ", memberId=" + memberId + ", childCommentsCount=" + childCommentsCount
//				+ ", likeCount=" + likeCount + ", dislikeCount=" + dislikeCount + ", content=" + content
//				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
//	}

	public Integer getChildCommentsCount() {
		return childCommentsCount;
	}

	public void setChildCommentsCount(Integer childCommentsCount) {
		this.childCommentsCount = childCommentsCount;
	}

	public static class Builder {
        private ExhibitionVO exhibition;
        private CommentVO parentComment;
        private MemberVO member;
        private String content;

        public Builder exhibition(ExhibitionVO exhibition) {
            this.exhibition = exhibition;
            return this;
        }

        public Builder parentComment(CommentVO parentComment) {
            this.parentComment = parentComment;
            return this;
        }

        public Builder member(MemberVO member) {
            this.member = member;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public CommentVO build() {
            CommentVO vo = new CommentVO();
            vo.exhibition = this.exhibition;
            vo.parentComment = this.parentComment;
            vo.member = this.member;
            vo.content = this.content;
            return vo;
        }
    }

}

/*
 * CREATE TABLE `comment` ( `comment_id` INT AUTO_INCREMENT PRIMARY KEY COMMENT
 * '流水號PK', `comment_status` VARCHAR(10) NOT NULL DEFAULT '正常' COMMENT '留言狀態',
 * `exhibition_id` INT NOT NULL COMMENT '展覽ID，FK', `member_id` INT NOT NULL
 * COMMENT '會員ID，FK', `parent_comment_id` INT NULL COMMENT '父層留言ID，自關聯FK',
 * `like_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '按讚數', `dislike_count`
 * INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '倒讚數', `content` VARCHAR(255) NOT
 * NULL COMMENT '留言內容', `created_at` TIMESTAMP NOT NULL DEFAULT
 * CURRENT_TIMESTAMP COMMENT '留言建立時間', `updated_at` TIMESTAMP NOT NULL DEFAULT
 * CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '留言最後編輯時間',
 * 
 * -- 外鍵 (除了 status 以外) CONSTRAINT `fk_comment_exhibition` FOREIGN KEY
 * (`exhibition_id`) REFERENCES `exhibition`(`exhibition_id`),
 * 
 * CONSTRAINT `fk_comment_member` FOREIGN KEY (`member_id`) REFERENCES
 * `member`(`member_id`),
 * 
 * CONSTRAINT `fk_comment_parent` FOREIGN KEY (`parent_comment_id`) REFERENCES
 * `comment`(`comment_id`) ON DELETE CASCADE );
 */