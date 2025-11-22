package kr.hhplus.be.server.point.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ChargePointResponse {
    private UUID userId;
    private int amount;
    private int balance;
}
