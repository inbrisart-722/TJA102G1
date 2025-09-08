package com.eventra.exhibitor_status.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exhibitor_status")
public class ExhibitorStatusVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_status_id")
    private Integer reviewStatusId;

    @NotNull
    @Column(name = "review_status")
    private String reviewStatus;

    @CreationTimestamp
    @Column(name = "review_status_created_at", nullable = false)
    private LocalDateTime reviewStatusCreatedAt;

    @UpdateTimestamp
    @Column(name = "review_status_updated_at", nullable = false)
    private LocalDateTime reviewStatusUpdatedAt;

    public ExhibitorStatusVO() {
    }

    public Integer getReviewStatusId() {
        return reviewStatusId;
    }

    public void setReviewStatusId(Integer reviewStatusId) {
        this.reviewStatusId = reviewStatusId;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
    }

    public LocalDateTime getReviewStatusCreatedAt() {
        return reviewStatusCreatedAt;
    }

    public void setReviewStatusCreatedAt(LocalDateTime reviewStatusCreatedAt) {
        this.reviewStatusCreatedAt = reviewStatusCreatedAt;
    }

    public LocalDateTime getReviewStatusUpdatedAt() {
        return reviewStatusUpdatedAt;
    }

    public void setReviewStatusUpdatedAt(LocalDateTime reviewStatusUpdatedAt) {
        this.reviewStatusUpdatedAt = reviewStatusUpdatedAt;
    }
}
