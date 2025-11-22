package kr.hhplus.be.server.point.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ChargePointRequest {
    private UUID userId;
    private int amount;
}
