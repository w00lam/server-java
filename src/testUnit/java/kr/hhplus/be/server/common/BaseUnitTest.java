package kr.hhplus.be.server.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.TimeZone;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public abstract class BaseUnitTest {

    protected final UUID FIXED_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    protected final UUID FIXED_UUID2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
    protected final LocalDateTime FIXED_NOW = LocalDateTime.of(2035, 12, 12, 12, 12);

    @BeforeEach
    void setup() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    protected UUID fixedUUID() {
        return FIXED_UUID;
    }
    protected UUID fixedUUID2() {
        return FIXED_UUID2;
    }

    protected LocalDateTime fixedNow() {
        return FIXED_NOW;
    }

}