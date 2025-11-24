package kr.hhplus.be.server.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.adapter.in.web.PaymentController;
import kr.hhplus.be.server.payment.domain.Payment;
import kr.hhplus.be.server.payment.domain.PaymentStatus;
import kr.hhplus.be.server.payment.dto.PaymentRequest;
import kr.hhplus.be.server.payment.usecase.MakePaymentUseCase;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MakePaymentUseCase makePaymentUseCase;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public MakePaymentUseCase makePaymentUseCase() {
            return Mockito.mock(MakePaymentUseCase.class);
        }
    }

    @Test
    @DisplayName("POST /payments 요청 시 결제 생성 후 PENDING 상태 응답을 반환한다")
    void createPayment_returnsPendingResponse() throws Exception {

        UUID paymentId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID reservationId = UUID.fromString("22222222-2222-2222-2222-222222222222");
        int amount = 15000;

        Payment mockPayment = new Payment(
                paymentId,
                reservationId,
                amount,
                PaymentStatus.PENDING,
                null,
                null,
                null,
                false
        );

        when(makePaymentUseCase.execute(any(UUID.class), anyInt())).thenReturn(mockPayment);

        PaymentRequest request = new PaymentRequest(reservationId, amount);

        mockMvc.perform(
                        post("/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(reservationId.toString()))
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}
