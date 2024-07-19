package com.tenten.studybadge.payment.controller;

import com.tenten.studybadge.common.security.CustomUserDetails;
import com.tenten.studybadge.common.security.LoginUser;
import com.tenten.studybadge.payment.dto.*;
import com.tenten.studybadge.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    @Operation(summary = "결제 요청", description = "토스페이먼츠에 결제 요청할 API", security = @SecurityRequirement(name = "bearerToken"))
    @Parameter(name = "paymentRequest", description = "결제 요청을 위한 값들")
    @PostMapping("/toss")
    public ResponseEntity<PaymentResponse> requestPayment(@LoginUser Long memberId,
                                                          @Valid @RequestBody PaymentRequest paymentRequest) {

        PaymentResponse response = paymentService.requestPayment(memberId, paymentRequest);

        return ResponseEntity.ok(response);
    }
    @Operation(summary = "결제 성공", description = "토스페이먼츠에서 결제 승인되어 성공 정보를 저장하는 API", security = @SecurityRequirement(name = "bearerToken"))
    @Parameter(name = "confirmRequest", description = "결제 승인을 위한 요청 값(paymentKey, orderId, amount)")
    @PostMapping("/success")
    public ResponseEntity<PaymentConfirm> confirmPayment(@Valid @RequestBody PaymentConfirmRequest confirmRequest) {

        PaymentConfirm confirm = paymentService.confirmPayment(confirmRequest);

        return ResponseEntity.ok(confirm);
    }
    @Operation(summary = "결제 실패", description = "결제 실패 정보를 받는 API", security = @SecurityRequirement(name = "bearerToken"))
    @Parameter(name = "failRequest", description = "결제 실패 정보를 받기 위한 요청 값(code, message, orderId)")
    @GetMapping("/fail")
    public ResponseEntity<PaymentFail> paymentFail(@RequestBody PaymentFailRequest failRequest) {

        PaymentFail fail = paymentService.paymentFail(failRequest);

        return ResponseEntity.ok(fail);
    }

    @Operation(summary = "결제 취소", description = "토스페이먼츠에 결제 취소 요청할 API", security = @SecurityRequirement(name = "bearerToken"))
    @Parameter(name = "cancelRequest", description = "결제 취소를 위한 요청 값(paymentKey, cancelReason)")
    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancelPayment(@LoginUser Long memberId,
                                                             @Valid @RequestBody PaymentCancelRequest cancelRequest) {

        Map<String, Object> response = paymentService.cancelPayment(memberId, cancelRequest);

        return ResponseEntity.ok(response);
    }
}
