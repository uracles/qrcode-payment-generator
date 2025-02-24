package com.digital_pay.application.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Entity
    @Data
    @Table(name = "transactions")
    public class Transaction {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private String id;

        private BigDecimal amount;
        private String currency;
        private Long merchantId;
        private Long userId;
        private String description;
        private String status;
        private LocalDateTime createdAt;
    }
