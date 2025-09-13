package com.eventra.exhibitionstatus.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exhibition_status")
public class ExhibitionStatusVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_status_id")
    private Integer exhibitionStatusId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "exhibition_status")
    private String exhibitionStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExhibitionStatusVO() {
    }

    public Integer getExhibitionStatusId() {
        return exhibitionStatusId;
    }

    public void setExhibitionStatusId(Integer exhibitionStatusId) {
        this.exhibitionStatusId = exhibitionStatusId;
    }

    public String getExhibitionStatus() {
        return exhibitionStatus;
    }

    public void setExhibitionStatus(String exhibitionStatus) {
        this.exhibitionStatus = exhibitionStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
