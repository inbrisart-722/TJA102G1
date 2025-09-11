package com.eventra.exhibition_ticket_type.model;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.eventra.exhibition.model.ExhibitionVO;
import com.eventra.order_item.model.OrderItemVO;
import com.eventra.ticket_type.model.TicketTypeVO;

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
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "exhibition_ticket_type")
public class ExhibitionTicketTypeVO{
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exhibition_ticket_type_id")
    private Integer exhibitionTicketTypeId;

    @Column(name = "exhibition_id", insertable = false, updatable = false)
	private Integer exhibitionId;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exhibition_id", nullable = false)
    private ExhibitionVO exhibition;

    @Column(name = "ticket_type_id", insertable = false, updatable = false)
    private Integer ticketTypeId;
    
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketTypeVO ticketType;

    @OneToMany(mappedBy = "exhibitionTicketType")
    private Set<OrderItemVO> orderItems;
    
    @NotNull(message = "請勿空白")
    @Column(name = "price")
    private Integer price;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Timestamp updatedAt;

    public ExhibitionTicketTypeVO() {
    }
    

	public Set<OrderItemVO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<OrderItemVO> orderItems) {
		this.orderItems = orderItems;
	}

	public ExhibitionVO getExhibition() {
		return exhibition;
	}

	public void setExhibitionId(Integer exhibitionId) {
		this.exhibitionId = exhibitionId;
	}

	public void setTicketTypeId(Integer ticketTypeId) {
		this.ticketTypeId = ticketTypeId;
	}

	public Integer getExhibitionTicketTypeId() {
        return exhibitionTicketTypeId;
    }

    public void setExhibitionTicketTypeId(Integer exhibitionTicketTypeId) {
        this.exhibitionTicketTypeId = exhibitionTicketTypeId;
    }

    public ExhibitionVO getExhibitionId() {
        return exhibition;
    }

    public void setExhibition(ExhibitionVO exhibition) {
        this.exhibition = exhibition;
    }

    public TicketTypeVO getTicketType() {
        return ticketType;
    }

    public void setTicketType(TicketTypeVO ticketType) {
        this.ticketType = ticketType;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
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
}
