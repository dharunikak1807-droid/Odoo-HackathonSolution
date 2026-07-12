package com.assetflow.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AllocationDTO {

    @Data
    public static class AllocateRequest {
        private Long assetId;
        private Long allocatedToId;
        private LocalDate expectedReturnDate;
        private String remarks;
    }

    @Data
    public static class ReturnRequest {
        private String returnConditionRating;
        private String remarks;
    }

    @Data
    public static class AllocationResponse {
        private Long id;
        private Long assetId;
        private String assetCode;
        private String assetName;
        private Long allocatedToId;
        private String allocatedToName;
        private Long allocatedById;
        private String allocatedByName;
        private LocalDateTime allocatedAt;
        private LocalDate expectedReturnDate;
        private LocalDateTime returnedAt;
        private String status;
        private String remarks;
    }
}
