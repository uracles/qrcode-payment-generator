package com.digital_pay.application.service;

import com.digital_pay.application.dto.PaymentRequest;
import com.digital_pay.application.dto.PaymentResponse;
import com.digital_pay.application.dto.QRCodeResponse;
import com.digital_pay.application.entity.Merchant;
import com.digital_pay.application.entity.Transaction;
import com.digital_pay.application.entity.User;
import com.digital_pay.application.repository.MerchantRepository;
import com.digital_pay.application.repository.TransactionRepository;
import com.digital_pay.application.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final TransactionRepository transactionRepository;
    private final QRCodeService qrCodeService;
    private final ObjectMapper objectMapper;

    public QRCodeResponse generateQRCode(PaymentRequest paymentRequest) throws Exception {
        validateEntities(paymentRequest.getUserId(), paymentRequest.getMerchantId());

        Transaction transaction = new Transaction();
        transaction.setAmount(paymentRequest.getAmount());
        transaction.setCurrency(paymentRequest.getCurrency());
        transaction.setMerchantId(paymentRequest.getMerchantId());
        transaction.setUserId(paymentRequest.getUserId());
        transaction.setDescription(paymentRequest.getDescription());
        transaction.setStatus("PENDING");
        transaction.setCreatedAt(LocalDateTime.now());

        transaction = transactionRepository.save(transaction);

        String qrContent = objectMapper.writeValueAsString(transaction.getId());
        String qrCodeBase64 = qrCodeService.generateQRCode(qrContent);

        return QRCodeResponse.builder()
                .qrCodeBase64(qrCodeBase64)
                .transactionId(transaction.getId())
                .paymentDetails(paymentRequest)
                .build();
    }

    @Transactional
    public PaymentResponse processPayment(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found"));

        User user = userRepository.findById(transaction.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Merchant merchant = merchantRepository.findById(transaction.getMerchantId())
                .orElseThrow(() -> new EntityNotFoundException("Merchant not found"));

        if (user.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        user.setBalance(user.getBalance().subtract(transaction.getAmount()));
        merchant.setBalance(merchant.getBalance().add(transaction.getAmount()));
        transaction.setStatus("COMPLETED");

        userRepository.save(user);
        merchantRepository.save(merchant);
        transactionRepository.save(transaction);

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(transaction.getAmount());
        paymentRequest.setCurrency(transaction.getCurrency());
        paymentRequest.setMerchantId(transaction.getMerchantId());
        paymentRequest.setUserId(transaction.getUserId());
        paymentRequest.setDescription(transaction.getDescription());

        return PaymentResponse.builder()
                .transactionId(transaction.getId())
                .status("SUCCESS")
                .userBalance(user.getBalance())
                .merchantBalance(merchant.getBalance())
                .paymentDetails(paymentRequest)
                .build();
    }

    private void validateEntities(Long userId, Long merchantId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User not found");
        }
        if (!merchantRepository.existsById(merchantId)) {
            throw new EntityNotFoundException("Merchant not found");
        }
    }
}
