package com.digital_pay.application.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;

    @Entity
    @Data
    @Table(name = "merchants")
    public class Merchant {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private BigDecimal balance;
    }
