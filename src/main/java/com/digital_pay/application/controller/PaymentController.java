package com.digital_pay.application.controller;

import com.digital_pay.application.dto.PaymentRequest;
import com.digital_pay.application.dto.PaymentResponse;
import com.digital_pay.application.dto.QRCodeResponse;
import com.digital_pay.application.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

    @RestController
    @RequestMapping("/api/payments")
    @RequiredArgsConstructor
    public class PaymentController {

        private final PaymentService paymentService;

        @PostMapping("/generate-qr")
        public ResponseEntity<QRCodeResponse> generateQRCode(@Valid @RequestBody PaymentRequest paymentRequest) throws Exception {
            QRCodeResponse response = paymentService.generateQRCode(paymentRequest);
            return ResponseEntity.ok(response);
        }

        @PostMapping("/process/{transactionId}")
        public ResponseEntity<PaymentResponse> processPayment(@PathVariable String transactionId) {
            PaymentResponse response = paymentService.processPayment(transactionId);
            return ResponseEntity.ok(response);
        }
    }
