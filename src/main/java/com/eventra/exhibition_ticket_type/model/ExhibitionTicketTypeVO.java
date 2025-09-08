package com.eventra.exhibition_ticket_type.model;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.ticket_type.model.TicketTypeVO;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "exhibition_ticket_type")
public class ExhibitionTicketTypeVO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_ticket_type_id")
    private Integer exhibitionTicketTypeId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private ExhibitionVO exhibitionId;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketTypeVO ticketTypeId;

    @NotNull(message = "請勿空白")
    @Column(name = "price")
    private Integer price;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ExhibitionTicketTypeVO() {
    }

    public Integer getExhibitionTicketTypeId() {
        return exhibitionTicketTypeId;
    }

    public void setExhibitionTicketTypeId(Integer exhibitionTicketTypeId) {
        this.exhibitionTicketTypeId = exhibitionTicketTypeId;
    }

    public ExhibitionVO getExhibitionId() {
        return exhibitionId;
    }

    public void setExhibitionId(ExhibitionVO exhibitionId) {
        this.exhibitionId = exhibitionId;
    }

    public TicketTypeVO getTicketTypeId() {
        return ticketTypeId;
    }

    public void setTicketTypeId(TicketTypeVO ticketTypeId) {
        this.ticketTypeId = ticketTypeId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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
