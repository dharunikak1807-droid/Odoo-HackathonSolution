package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "allocations")
public class Allocation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asset_id", nullable = false)
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "allocated_to_id", nullable = false)
    private User allocatedTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "allocated_by_id")
    private User allocatedBy;

    private LocalDateTime allocatedAt;

    private LocalDate expectedReturnDate;

    private LocalDateTime returnedAt;

    @Column(length = 30)
    private String returnConditionRating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AllocationStatus status = AllocationStatus.ACTIVE;

    private String remarks;
}
